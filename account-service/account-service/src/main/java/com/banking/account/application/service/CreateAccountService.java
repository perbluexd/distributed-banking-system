package com.banking.account.application.service;

import com.banking.account.application.command.CreateAccountCommand;
import com.banking.account.application.error.ErrorCode;
import com.banking.account.application.error.NotFoundException;
import com.banking.account.application.error.ValidationException;
import com.banking.account.application.port.in.CreateAccountUseCase;
import com.banking.account.application.port.out.AccountRepositoryPort;
import com.banking.account.application.port.out.CustomerSnapshotRepositoryPort;
import com.banking.account.application.port.out.OutboxEventRepositoryPort;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.CustomerId;
import com.banking.account.domain.model.OutboxEvent;
import com.banking.account.infrastructure.messaging.event.AccountCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

public class CreateAccountService implements CreateAccountUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateAccountService.class);

    private static final String AGGREGATE_TYPE = "Account";
    private static final String EVENT_TYPE = "AccountCreatedEvent";
    private static final String TOPIC = "account.created";

    private final AccountRepositoryPort accountRepositoryPort;
    private final CustomerSnapshotRepositoryPort customerSnapshotRepositoryPort;
    private final OutboxEventRepositoryPort outboxEventRepositoryPort;
    private final ObjectMapper objectMapper;

    public CreateAccountService(
            AccountRepositoryPort accountRepositoryPort,
            CustomerSnapshotRepositoryPort customerSnapshotRepositoryPort,
            OutboxEventRepositoryPort outboxEventRepositoryPort,
            ObjectMapper objectMapper
    ) {
        this.accountRepositoryPort = accountRepositoryPort;
        this.customerSnapshotRepositoryPort = customerSnapshotRepositoryPort;
        this.outboxEventRepositoryPort = outboxEventRepositoryPort;
        this.objectMapper = objectMapper;
    }

    @Override
    public Account createAccount(CreateAccountCommand command) {
        validateCommand(command);

        CustomerId customerId = CustomerId.of(command.customerId());

        if (!customerSnapshotRepositoryPort.existsByCustomerId(customerId)) {
            throw new NotFoundException(
                    ErrorCode.CUSTOMER_NOT_FOUND,
                    "Customer snapshot not found"
            );
        }

        String accountNumber = generateUniqueAccountNumber();

        Account account = Account.create(
                customerId,
                accountNumber,
                command.accountType(),
                command.currency()
        );

        Account savedAccount = accountRepositoryPort.save(account);

        log.info(
                "[ACCOUNT] Account created successfully. customerId={}, accountId={}, accountNumber={}, accountType={}, currency={}, balance={}",
                savedAccount.getCustomerId().value(),
                savedAccount.getId().value(),
                savedAccount.getAccountNumber(),
                savedAccount.getType(),
                savedAccount.getCurrency(),
                savedAccount.getBalance().amount()
        );

        AccountCreatedEvent event = new AccountCreatedEvent(
                UUID.randomUUID(),
                savedAccount.getId().value(),
                savedAccount.getCustomerId().value(),
                savedAccount.getAccountNumber(),
                savedAccount.getType(),
                savedAccount.getBalance().amount(),
                savedAccount.getCurrency(),
                Instant.now()
        );

        OutboxEvent outboxEvent = OutboxEvent.create(
                AGGREGATE_TYPE,
                savedAccount.getId().value(),
                EVENT_TYPE,
                TOPIC,
                savedAccount.getId().value().toString(),
                toJson(event)
        );

        outboxEventRepositoryPort.save(outboxEvent);

        log.info(
                "[ACCOUNT] Outbox event saved. eventType={}, topic={}, aggregateType={}, aggregateId={}, eventKey={}, customerId={}, accountId={}",
                EVENT_TYPE,
                TOPIC,
                AGGREGATE_TYPE,
                savedAccount.getId().value(),
                savedAccount.getId().value(),
                savedAccount.getCustomerId().value(),
                savedAccount.getId().value()
        );

        return savedAccount;
    }

    private void validateCommand(CreateAccountCommand command) {
        if (command == null) {
            throw new ValidationException(
                    ErrorCode.VALIDATION_ERROR,
                    "CreateAccountCommand cannot be null"
            );
        }

        if (command.customerId() == null) {
            throw new ValidationException(
                    ErrorCode.VALIDATION_ERROR,
                    "Customer id is required"
            );
        }

        if (command.accountType() == null) {
            throw new ValidationException(
                    ErrorCode.INVALID_ACCOUNT_TYPE,
                    "Account type is required"
            );
        }

        if (command.currency() == null) {
            throw new ValidationException(
                    ErrorCode.INVALID_CURRENCY,
                    "Currency is required"
            );
        }
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;

        do {
            accountNumber = "ACC-" + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 12)
                    .toUpperCase();
        } while (accountRepositoryPort.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    private String toJson(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error serializing event to JSON", e);
        }
    }
}