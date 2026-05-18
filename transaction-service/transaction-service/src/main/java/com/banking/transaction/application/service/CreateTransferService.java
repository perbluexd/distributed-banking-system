package com.banking.transaction.application.service;

import com.banking.transaction.application.command.CreateTransferCommand;
import com.banking.transaction.application.command.DebitAccountCommand;
import com.banking.transaction.application.error.AccountSnapshotNotFoundException;
import com.banking.transaction.application.error.InvalidTransferException;
import com.banking.transaction.application.port.in.CreateTransferUseCase;
import com.banking.transaction.application.port.out.*;
import com.banking.transaction.domain.model.*;
import com.banking.transaction.infrastructure.messaging.event.TransferCreatedEvent;
import com.banking.transaction.infrastructure.messaging.event.TransferFailedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.UUID;

@Service
public class CreateTransferService implements CreateTransferUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateTransferService.class);

    private static final String TRANSFER_CREATED_TOPIC = "transfer.created";
    private static final String TRANSFER_FAILED_TOPIC = "transfer.failed";

    private final TransferRepositoryPort transferRepositoryPort;
    private final AccountSnapshotRepositoryPort accountSnapshotRepositoryPort;
    private final IdempotencyRepositoryPort idempotencyRepositoryPort;
    private final OutboxEventRepositoryPort outboxEventRepositoryPort;
    private final AccountCommandPort accountCommandPort;
    private final ObjectMapper objectMapper;

    public CreateTransferService(
            TransferRepositoryPort transferRepositoryPort,
            AccountSnapshotRepositoryPort accountSnapshotRepositoryPort,
            IdempotencyRepositoryPort idempotencyRepositoryPort,
            OutboxEventRepositoryPort outboxEventRepositoryPort,
            AccountCommandPort accountCommandPort,
            ObjectMapper objectMapper
    ) {
        this.transferRepositoryPort = transferRepositoryPort;
        this.accountSnapshotRepositoryPort = accountSnapshotRepositoryPort;
        this.idempotencyRepositoryPort = idempotencyRepositoryPort;
        this.outboxEventRepositoryPort = outboxEventRepositoryPort;
        this.accountCommandPort = accountCommandPort;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public UUID createTransfer(CreateTransferCommand command) {
        IdempotencyKey idempotencyKey = new IdempotencyKey(command.idempotencyKey());

        return idempotencyRepositoryPort.findTransferIdByKey(idempotencyKey)
                .map(existingTransferId -> {
                    log.info(
                            "[TRANSACTION] Idempotent transfer request detected. idempotencyKey={}, existingTransferId={}",
                            command.idempotencyKey(),
                            existingTransferId
                    );

                    return existingTransferId;
                })
                .orElseGet(() -> createNewTransfer(command, idempotencyKey));
    }

    private UUID createNewTransfer(CreateTransferCommand command, IdempotencyKey idempotencyKey) {
        AccountId sourceAccountId = new AccountId(command.sourceAccountId());
        AccountId targetAccountId = new AccountId(command.targetAccountId());
        Money amount = new Money(command.amount(), command.currency());

        log.info(
                "[TRANSACTION] Creating new transfer. sourceAccountId={}, targetAccountId={}, amount={}, currency={}, idempotencyKey={}",
                command.sourceAccountId(),
                command.targetAccountId(),
                command.amount(),
                command.currency(),
                command.idempotencyKey()
        );

        AccountSnapshot sourceSnapshot = accountSnapshotRepositoryPort.findByAccountId(sourceAccountId)
                .orElseThrow(() -> new AccountSnapshotNotFoundException(command.sourceAccountId()));

        AccountSnapshot targetSnapshot = accountSnapshotRepositoryPort.findByAccountId(targetAccountId)
                .orElseThrow(() -> new AccountSnapshotNotFoundException(command.targetAccountId()));

        Transfer transfer = Transfer.create(
                sourceAccountId,
                targetAccountId,
                amount,
                TransferType.INTERNAL,
                idempotencyKey
        );

        Transfer savedTransfer = transferRepositoryPort.save(transfer);

        idempotencyRepositoryPort.save(idempotencyKey, savedTransfer.getId().value());

        try {
            sourceSnapshot.validateCanTransfer(amount);
            validateTargetAccount(targetSnapshot, amount);
        } catch (RuntimeException ex) {
            savedTransfer.markFailed();

            Transfer failedTransfer = transferRepositoryPort.save(savedTransfer);

            saveTransferFailedOutboxEvent(failedTransfer, ex.getMessage());

            log.info(
                    "[TRANSACTION] Transfer failed during initial validation. transferId={}, reason={}",
                    failedTransfer.getId().value(),
                    ex.getMessage()
            );

            return failedTransfer.getId().value();
        }

        log.info(
                "[TRANSACTION] Transfer created successfully. transferId={}, sourceAccountId={}, targetAccountId={}, amount={}, currency={}, status={}",
                savedTransfer.getId().value(),
                savedTransfer.getSourceAccountId().value(),
                savedTransfer.getTargetAccountId().value(),
                savedTransfer.getAmount().amount(),
                savedTransfer.getAmount().currency(),
                savedTransfer.getStatus()
        );

        TransferCreatedEvent payload = new TransferCreatedEvent(
                UUID.randomUUID(),
                savedTransfer.getId().value(),
                savedTransfer.getSourceAccountId().value(),
                savedTransfer.getTargetAccountId().value(),
                savedTransfer.getAmount().amount(),
                savedTransfer.getAmount().currency(),
                savedTransfer.getStatus(),
                Instant.now()
        );

        OutboxEvent outboxEvent = OutboxEvent.create(
                "Transfer",
                savedTransfer.getId().value(),
                "TransferCreatedEvent",
                TRANSFER_CREATED_TOPIC,
                savedTransfer.getId().value().toString(),
                toJson(payload)
        );

        outboxEventRepositoryPort.save(outboxEvent);

        log.info(
                "[TRANSACTION] Outbox event saved. eventType={}, topic={}, aggregateType={}, aggregateId={}, eventKey={}, transferId={}, status={}",
                "TransferCreatedEvent",
                TRANSFER_CREATED_TOPIC,
                "Transfer",
                savedTransfer.getId().value(),
                savedTransfer.getId().value(),
                savedTransfer.getId().value(),
                savedTransfer.getStatus()
        );

        registerDebitAfterCommit(savedTransfer);

        return savedTransfer.getId().value();
    }

    private void validateTargetAccount(AccountSnapshot targetSnapshot, Money amount) {
        if (!targetSnapshot.isActive()) {
            throw new InvalidTransferException("Target account is not active");
        }

        if (!targetSnapshot.getCurrency().equals(amount.currency())) {
            throw new InvalidTransferException("Target account currency mismatch");
        }
    }

    private UUID getSourceCustomerId(Transfer transfer) {
        AccountSnapshot sourceSnapshot = accountSnapshotRepositoryPort
                .findByAccountId(transfer.getSourceAccountId())
                .orElseThrow(() -> new AccountSnapshotNotFoundException(
                        transfer.getSourceAccountId().value()
                ));

        return sourceSnapshot.getCustomerId().value();
    }

    private void saveTransferFailedOutboxEvent(Transfer transfer, String reason) {
        UUID customerId = getSourceCustomerId(transfer);

        TransferFailedEvent payload = new TransferFailedEvent(
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

    private void registerDebitAfterCommit(Transfer transfer) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }

        log.info(
                "[TRANSACTION] Registering debit command after commit. transferId={}, sourceAccountId={}, amount={}, currency={}",
                transfer.getId().value(),
                transfer.getSourceAccountId().value(),
                transfer.getAmount().amount(),
                transfer.getAmount().currency()
        );

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.info(
                        "[TRANSACTION] Sending debit command to Account Service after transfer commit. transferId={}, sourceAccountId={}, amount={}, currency={}",
                        transfer.getId().value(),
                        transfer.getSourceAccountId().value(),
                        transfer.getAmount().amount(),
                        transfer.getAmount().currency()
                );

                accountCommandPort.debit(new DebitAccountCommand(
                        transfer.getSourceAccountId().value(),
                        transfer.getAmount().amount(),
                        transfer.getAmount().currency(),
                        transfer.getId().value()
                ));
            }
        });
    }

    private String toJson(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error serializing transfer event", e);
        }
    }
}