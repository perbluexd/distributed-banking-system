package com.banking.transaction.infrastructure.messaging.consumer;

import com.banking.transaction.application.service.AccountEventHandlerService;
import com.banking.transaction.infrastructure.messaging.event.AccountDebitedEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class AccountDebitedConsumer {

    private final AccountEventHandlerService accountEventHandlerService;

    public AccountDebitedConsumer(AccountEventHandlerService accountEventHandlerService) {
        this.accountEventHandlerService = accountEventHandlerService;
    }

    @KafkaListener(
            topics = "account.debited",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "accountDebitedKafkaListenerContainerFactory"
    )
    public void consume(AccountDebitedEvent event) {
        accountEventHandlerService.handleAccountDebited(
                event.transferId(),
                event.accountId(),
                event.customerId(),
                event.currency(),
                event.balance()
        );
    }
}