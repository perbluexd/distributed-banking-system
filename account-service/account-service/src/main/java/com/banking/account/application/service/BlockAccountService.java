package com.banking.account.application.service;

import com.banking.account.application.command.BlockAccountCommand;
import com.banking.account.application.error.ConflictException;
import com.banking.account.application.error.ErrorCode;
import com.banking.account.application.error.NotFoundException;
import com.banking.account.application.error.ValidationException;
import com.banking.account.application.port.in.BlockAccountUseCase;
import com.banking.account.application.port.out.AccountRepositoryPort;
import com.banking.account.application.port.out.OutboxEventRepositoryPort;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.AccountId;
import com.banking.account.domain.model.AccountStatus;
import com.banking.account.domain.model.OutboxEvent;
import com.banking.account.infrastructure.messaging.event.AccountBlockedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;

public class BlockAccountService implements BlockAccountUseCase {

    private final AccountRepositoryPort accountRepositoryPort;
    private final OutboxEventRepositoryPort outboxEventRepositoryPort;
    private final ObjectMapper objectMapper;

    public BlockAccountService(
            AccountRepositoryPort accountRepositoryPort,
            OutboxEventRepositoryPort outboxEventRepositoryPort,
            ObjectMapper objectMapper
    ) {
        this.accountRepositoryPort = accountRepositoryPort;
        this.outboxEventRepositoryPort = outboxEventRepositoryPort;
        this.objectMapper = objectMapper;
    }

    @Override
    public Account blockAccount(BlockAccountCommand command) {
        if (command == null || command.accountId() == null) {
            throw new ValidationException(
                    ErrorCode.VALIDATION_ERROR,
                    "Account id is required"
            );
        }

        AccountId accountId = AccountId.of(command.accountId());

        Account account = accountRepositoryPort.findById(accountId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.ACCOUNT_NOT_FOUND,
                        "Account not found"
                ));

        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new ConflictException(
                    ErrorCode.ACCOUNT_ALREADY_BLOCKED,
                    "Account is already blocked"
            );
        }

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new ConflictException(
                    ErrorCode.ACCOUNT_CLOSED,
                    "Closed account cannot be blocked"
            );
        }

        account.block();

        Account savedAccount = accountRepositoryPort.save(account);

        AccountBlockedEvent event = new AccountBlockedEvent(
                java.util.UUID.randomUUID(),
                savedAccount.getId().value(),
                savedAccount.getCustomerId().value(),
                savedAccount.getAccountNumber(),
                Instant.now()
        );

        OutboxEvent outboxEvent = OutboxEvent.create(
                "Account",
                savedAccount.getId().value(),
                "AccountBlockedEvent",
                "account.blocked",
                savedAccount.getId().value().toString(),
                toJson(event)
        );

        outboxEventRepositoryPort.save(outboxEvent);

        return savedAccount;
    }

    private String toJson(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error serializing event to JSON", e);
        }
    }
}