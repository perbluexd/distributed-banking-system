package com.banking.transaction.application.service;

import com.banking.transaction.application.command.CreditAccountCommand;
import com.banking.transaction.application.error.AccountSnapshotNotFoundException;
import com.banking.transaction.application.error.TransferNotFoundException;
import com.banking.transaction.application.port.out.AccountCommandPort;
import com.banking.transaction.application.port.out.AccountSnapshotRepositoryPort;
import com.banking.transaction.application.port.out.OutboxEventRepositoryPort;
import com.banking.transaction.application.port.out.TransferRepositoryPort;
import com.banking.transaction.domain.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class AccountEventHandlerService {

    private static final Logger log = LoggerFactory.getLogger(AccountEventHandlerService.class);

    private static final String TRANSFER_COMPLETED_TOPIC = "transfer.completed";
    private static final String TRANSFER_FAILED_TOPIC = "transfer.failed";

    private final AccountSnapshotRepositoryPort accountSnapshotRepositoryPort;
    private final TransferRepositoryPort transferRepositoryPort;
    private final AccountCommandPort accountCommandPort;
    private final OutboxEventRepositoryPort outboxEventRepositoryPort;
    private final ObjectMapper objectMapper;

    public AccountEventHandlerService(
            AccountSnapshotRepositoryPort accountSnapshotRepositoryPort,
            TransferRepositoryPort transferRepositoryPort,
            AccountCommandPort accountCommandPort,
            OutboxEventRepositoryPort outboxEventRepositoryPort,
            ObjectMapper objectMapper
    ) {
        this.accountSnapshotRepositoryPort = accountSnapshotRepositoryPort;
        this.transferRepositoryPort = transferRepositoryPort;
        this.accountCommandPort = accountCommandPort;
        this.outboxEventRepositoryPort = outboxEventRepositoryPort;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void handleAccountCreated(
            UUID accountId,
            UUID customerId,
            Currency currency,
            BigDecimal balance
    ) {
        log.info(
                "[TRANSACTION] Handling AccountCreatedEvent. eventType={}, accountId={}, customerId={}, currency={}, balance={}",
                "AccountCreatedEvent",
                accountId,
                customerId,
                currency,
                balance
        );

        AccountSnapshot snapshot = new AccountSnapshot(
                new AccountId(accountId),
                new CustomerId(customerId),
                currency,
                balance,
                true
        );

        accountSnapshotRepositoryPort.save(snapshot);

        log.info(
                "[TRANSACTION] AccountSnapshot saved from AccountCreatedEvent. accountId={}, customerId={}, currency={}, balance={}, active={}",
                accountId,
                customerId,
                currency,
                balance,
                true
        );
    }

    @Transactional
    public void handleAccountActivated(UUID accountId) {
        AccountSnapshot current = getSnapshot(accountId);

        AccountSnapshot updated = new AccountSnapshot(
                current.getAccountId(),
                current.getCustomerId(),
                current.getCurrency(),
                current.getBalance(),
                true
        );

        accountSnapshotRepositoryPort.save(updated);
    }

    @Transactional
    public void handleAccountBlocked(UUID accountId) {
        AccountSnapshot current = getSnapshot(accountId);

        AccountSnapshot updated = new AccountSnapshot(
                current.getAccountId(),
                current.getCustomerId(),
                current.getCurrency(),
                current.getBalance(),
                false
        );

        accountSnapshotRepositoryPort.save(updated);
    }

    @Transactional
    public void handleAccountDebited(
            UUID transferId,
            UUID accountId,
            UUID customerId,
            Currency currency,
            BigDecimal newBalance
    ) {
        log.info(
                "[TRANSACTION] Handling AccountDebitedEvent. eventType={}, transferId={}, accountId={}, customerId={}, currency={}, newBalance={}",
                "AccountDebitedEvent",
                transferId,
                accountId,
                customerId,
                currency,
                newBalance
        );

        AccountSnapshot updated = new AccountSnapshot(
                new AccountId(accountId),
                new CustomerId(customerId),
                currency,
                newBalance,
                true
        );

        accountSnapshotRepositoryPort.save(updated);

        log.info(
                "[TRANSACTION] AccountSnapshot updated after debit. transferId={}, accountId={}, customerId={}, newBalance={}",
                transferId,
                accountId,
                customerId,
                newBalance
        );

        Transfer transfer = transferRepositoryPort.findById(new TransferId(transferId))
                .orElseThrow(() -> new TransferNotFoundException(transferId));

        log.info(
                "[TRANSACTION] Transfer found after debit. transferId={}, sourceAccountId={}, targetAccountId={}, amount={}, currency={}, status={}",
                transfer.getId().value(),
                transfer.getSourceAccountId().value(),
                transfer.getTargetAccountId().value(),
                transfer.getAmount().amount(),
                transfer.getAmount().currency(),
                transfer.getStatus()
        );

        registerCreditAfterCommit(transfer);
    }

    @Transactional
    public void handleAccountCredited(
            UUID transferId,
            UUID accountId,
            UUID customerId,
            Currency currency,
            BigDecimal newBalance
    ) {
        log.info(
                "[TRANSACTION] Handling AccountCreditedEvent. eventType={}, transferId={}, accountId={}, customerId={}, currency={}, newBalance={}",
                "AccountCreditedEvent",
                transferId,
                accountId,
                customerId,
                currency,
                newBalance
        );

        AccountSnapshot updated = new AccountSnapshot(
                new AccountId(accountId),
                new CustomerId(customerId),
                currency,
                newBalance,
                true
        );

        accountSnapshotRepositoryPort.save(updated);

        log.info(
                "[TRANSACTION] AccountSnapshot updated after credit. transferId={}, accountId={}, customerId={}, newBalance={}",
                transferId,
                accountId,
                customerId,
                newBalance
        );

        transferRepositoryPort.findById(new TransferId(transferId))
                .ifPresentOrElse(transfer -> {
                    transfer.markCompleted();

                    Transfer savedTransfer = transferRepositoryPort.save(transfer);

                    log.info(
                            "[TRANSACTION] Transfer marked as COMPLETED. transferId={}, sourceAccountId={}, targetAccountId={}, amount={}, currency={}, status={}",
                            savedTransfer.getId().value(),
                            savedTransfer.getSourceAccountId().value(),
                            savedTransfer.getTargetAccountId().value(),
                            savedTransfer.getAmount().amount(),
                            savedTransfer.getAmount().currency(),
                            savedTransfer.getStatus()
                    );

                    saveTransferCompletedOutboxEvent(savedTransfer);
                }, () -> {
                    log.warn(
                            "[TRANSACTION] AccountCreditedEvent ignored for saga completion. Transfer not found. transferId={}, accountId={}, customerId={}",
                            transferId,
                            accountId,
                            customerId
                    );

                    System.out.println("Account credited event ignored for saga completion. Transfer not found: " + transferId);
                });
    }

    @Transactional
    public void handleTransferFailed(UUID transferId) {
        Transfer transfer = transferRepositoryPort.findById(new TransferId(transferId))
                .orElseThrow(() -> new TransferNotFoundException(transferId));

        transfer.markFailed();

        Transfer savedTransfer = transferRepositoryPort.save(transfer);

        log.info(
                "[TRANSACTION] Transfer marked as FAILED. transferId={}, sourceAccountId={}, targetAccountId={}, amount={}, currency={}, status={}",
                savedTransfer.getId().value(),
                savedTransfer.getSourceAccountId().value(),
                savedTransfer.getTargetAccountId().value(),
                savedTransfer.getAmount().amount(),
                savedTransfer.getAmount().currency(),
                savedTransfer.getStatus()
        );

        saveTransferFailedOutboxEvent(savedTransfer, "Transfer failed");
    }

    private AccountSnapshot getSnapshot(UUID accountId) {
        return accountSnapshotRepositoryPort.findByAccountId(new AccountId(accountId))
                .orElseThrow(() -> new AccountSnapshotNotFoundException(accountId));
    }

    private void registerCreditAfterCommit(Transfer transfer) {
        log.info(
                "[TRANSACTION] Registering credit command after commit. transferId={}, targetAccountId={}, amount={}, currency={}",
                transfer.getId().value(),
                transfer.getTargetAccountId().value(),
                transfer.getAmount().amount(),
                transfer.getAmount().currency()
        );

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.info(
                        "[TRANSACTION] Sending credit command to Account Service after debit commit. transferId={}, targetAccountId={}, amount={}, currency={}",
                        transfer.getId().value(),
                        transfer.getTargetAccountId().value(),
                        transfer.getAmount().amount(),
                        transfer.getAmount().currency()
                );

                accountCommandPort.credit(new CreditAccountCommand(
                        transfer.getTargetAccountId().value(),
                        transfer.getAmount().amount(),
                        transfer.getAmount().currency(),
                        transfer.getId().value()
                ));
            }
        });
    }

    private UUID getSourceCustomerId(Transfer transfer) {
        AccountSnapshot sourceSnapshot = accountSnapshotRepositoryPort
                .findByAccountId(transfer.getSourceAccountId())
                .orElseThrow(() -> new AccountSnapshotNotFoundException(
                        transfer.getSourceAccountId().value()
                ));

        return sourceSnapshot.getCustomerId().value();
    }

    private void saveTransferCompletedOutboxEvent(Transfer transfer) {
        UUID customerId = getSourceCustomerId(transfer);

        TransferCompletedPayload payload = new TransferCompletedPayload(
                UUID.randomUUID(),
                transfer.getId().value(),
                customerId,
                transfer.getSourceAccountId().value(),
                transfer.getTargetAccountId().value(),
                transfer.getAmount().amount(),
                transfer.getAmount().currency(),
                transfer.getStatus(),
                Instant.now()
        );

        OutboxEvent outboxEvent = OutboxEvent.create(
                "Transfer",
                transfer.getId().value(),
                "TransferCompletedEvent",
                TRANSFER_COMPLETED_TOPIC,
                transfer.getId().value().toString(),
                toJson(payload)
        );

        outboxEventRepositoryPort.save(outboxEvent);

        log.info(
                "[TRANSACTION] Outbox event saved. eventType={}, topic={}, aggregateType={}, aggregateId={}, eventKey={}, transferId={}, customerId={}, status={}",
                "TransferCompletedEvent",
                TRANSFER_COMPLETED_TOPIC,
                "Transfer",
                transfer.getId().value(),
                transfer.getId().value(),
                transfer.getId().value(),
                customerId,
                transfer.getStatus()
        );
    }

    private void saveTransferFailedOutboxEvent(Transfer transfer, String reason) {
        UUID customerId = getSourceCustomerId(transfer);

        TransferFailedPayload payload = new TransferFailedPayload(
                UUID.randomUUID(),
                transfer.getId().value(),
                customerId,
                transfer.getSourceAccountId().value(),
                transfer.getTargetAccountId().value(),
                transfer.getAmount().amount(),
                transfer.getAmount().currency(),
                transfer.getStatus(),
                reason,
                Instant.now()
        );

        OutboxEvent outboxEvent = OutboxEvent.create(
                "Transfer",
                transfer.getId().value(),
                "TransferFailedEvent",
                TRANSFER_FAILED_TOPIC,
                transfer.getId().value().toString(),
                toJson(payload)
        );

        outboxEventRepositoryPort.save(outboxEvent);

        log.info(
                "[TRANSACTION] Outbox event saved. eventType={}, topic={}, aggregateType={}, aggregateId={}, eventKey={}, transferId={}, customerId={}, status={}, reason={}",
                "TransferFailedEvent",
                TRANSFER_FAILED_TOPIC,
                "Transfer",
                transfer.getId().value(),
                transfer.getId().value(),
                transfer.getId().value(),
                customerId,
                transfer.getStatus(),
                reason
        );
    }

    private String toJson(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error serializing transfer event", e);
        }
    }

    private record TransferCompletedPayload(
            UUID eventId,
            UUID transferId,
            UUID customerId,
            UUID sourceAccountId,
            UUID targetAccountId,
            BigDecimal amount,
            Currency currency,
            TransferStatus status,
            Instant occurredAt
    ) {
    }

    private record TransferFailedPayload(
            UUID eventId,
            UUID transferId,
            UUID customerId,
            UUID sourceAccountId,
            UUID targetAccountId,
            BigDecimal amount,
            Currency currency,
            TransferStatus status,
            String reason,
            Instant occurredAt
    ) {
    }
}