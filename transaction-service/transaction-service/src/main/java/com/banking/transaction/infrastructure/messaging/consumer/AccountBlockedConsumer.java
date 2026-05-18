package com.banking.transaction.infrastructure.messaging.consumer;

import com.banking.transaction.application.service.AccountEventHandlerService;
import com.banking.transaction.infrastructure.messaging.event.AccountBlockedEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class AccountBlockedConsumer {

    private final AccountEventHandlerService accountEventHandlerService;

    public AccountBlockedConsumer(AccountEventHandlerService accountEventHandlerService) {
        this.accountEventHandlerService = accountEventHandlerService;
    }

    @KafkaListener(
            topics = "account.blocked",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "accountBlockedKafkaListenerContainerFactory"
    )
    public void consume(AccountBlockedEvent event) {
        accountEventHandlerService.handleAccountBlocked(event.accountId());
    }
}