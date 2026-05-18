package com.banking.transaction.infrastructure.messaging.consumer;

import com.banking.transaction.application.service.AccountEventHandlerService;
import com.banking.transaction.infrastructure.messaging.event.AccountActivatedEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class AccountActivatedConsumer {

    private final AccountEventHandlerService accountEventHandlerService;

    public AccountActivatedConsumer(AccountEventHandlerService accountEventHandlerService) {
        this.accountEventHandlerService = accountEventHandlerService;
    }

    @KafkaListener(
            topics = "account.activated",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "accountActivatedKafkaListenerContainerFactory"
    )
    public void consume(AccountActivatedEvent event) {
        accountEventHandlerService.handleAccountActivated(event.accountId());
    }
}