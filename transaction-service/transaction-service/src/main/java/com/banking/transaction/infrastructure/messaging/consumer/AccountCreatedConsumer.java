package com.banking.transaction.infrastructure.messaging.consumer;

import com.banking.transaction.application.service.AccountEventHandlerService;
import com.banking.transaction.infrastructure.messaging.event.AccountCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class AccountCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(AccountCreatedConsumer.class);

    private final AccountEventHandlerService accountEventHandlerService;

    public AccountCreatedConsumer(AccountEventHandlerService accountEventHandlerService) {
        this.accountEventHandlerService = accountEventHandlerService;
    }

    @KafkaListener(
            topics = "account.created",
            containerFactory = "accountCreatedKafkaListenerContainerFactory",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(AccountCreatedEvent event) {

        log.info(
                "[TRANSACTION] AccountCreatedEvent received. eventType={}, topic={}, customerId={}, accountId={}, currency={}, balance={}",
                "AccountCreatedEvent",
                "account.created",
                event.customerId(),
                event.accountId(),
                event.currency(),
                event.balance()
        );

        accountEventHandlerService.handleAccountCreated(
                event.accountId(),
                event.customerId(),
                event.currency(),
                event.balance()
        );

        log.info(
                "[TRANSACTION] AccountCreatedEvent processed successfully. customerId={}, accountId={}",
                event.customerId(),
                event.accountId()
        );
    }
}