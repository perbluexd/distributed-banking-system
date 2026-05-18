package com.banking.account.application.service;

import com.banking.account.application.command.CreditAccountCommand;
import com.banking.account.application.port.in.CreditAccountUseCase;
import com.banking.account.application.port.out.AccountRepositoryPort;
import com.banking.account.application.port.out.OutboxEventRepositoryPort;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.AccountId;
import com.banking.account.domain.model.Money;
import com.banking.account.domain.model.OutboxEvent;
import com.banking.account.infrastructure.messaging.event.AccountCreditedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class CreditAccountService implements CreditAccountUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreditAccountService.class);

    private static final String AGGREGATE_TYPE = "Account";
    private static final String EVENT_TYPE = "AccountCreditedEvent";
    private static final String TOPIC = "account.credited";

    private final AccountRepositoryPort accountRepositoryPort;
    private final OutboxEventRepositoryPort outboxEventRepositoryPort;
    private final ObjectMapper objectMapper;

    public CreditAccountService(
            AccountRepositoryPort accountRepositoryPort,
            OutboxEventRepositoryPort outboxEventRepositoryPort,
            ObjectMapper objectMapper
    ) {
        this.accountRepositoryPort = accountRepositoryPort;
        this.outboxEventRepositoryPort = outboxEventRepositoryPort;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Account credit(CreditAccountCommand command) {
        Account account = accountRepositoryPort.findById(new AccountId(command.accountId()))
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Money amount = new Money(command.amount(), command.currency());

        account.credit(amount);

        Account savedAccount = accountRepositoryPort.save(account);

        log.info(
                "[ACCOUNT] Account credited successfully. transferId={}, customerId={}, accountId={}, amount={}, currency={}, newBalance={}",
                command.transferId(),
                savedAccount.getCustomerId().value(),
                savedAccount.getId().value(),
                amount.amount(),
                savedAccount.getCurrency(),
                savedAccount.getBalance().amount()
        );

        AccountCreditedEvent event = new AccountCreditedEvent(
                java.util.UUID.randomUUID(),
                command.transferId(),
                savedAccount.getId().value(),
                savedAccount.getCustomerId().value(),
                savedAccount.getAccountNumber(),
                amount.amount(),
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
                "[ACCOUNT] Outbox event saved. eventType={}, topic={}, aggregateType={}, aggregateId={}, eventKey={}, transferId={}, customerId={}, accountId={}",
                EVENT_TYPE,
                TOPIC,
                AGGREGATE_TYPE,
                savedAccount.getId().value(),
                savedAccount.getId().value(),
                command.transferId(),
                savedAccount.getCustomerId().value(),
                savedAccount.getId().value()
        );

        return savedAccount;
    }

    private String toJson(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error serializing event", e);
        }
    }
}