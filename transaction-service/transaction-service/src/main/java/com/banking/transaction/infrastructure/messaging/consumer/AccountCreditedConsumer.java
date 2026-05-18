package com.banking.transaction.infrastructure.messaging.consumer;

import com.banking.transaction.application.service.AccountEventHandlerService;
import com.banking.transaction.infrastructure.messaging.event.AccountCreditedEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class AccountCreditedConsumer {

    private final AccountEventHandlerService accountEventHandlerService;

    public AccountCreditedConsumer(AccountEventHandlerService accountEventHandlerService) {
        this.accountEventHandlerService = accountEventHandlerService;
    }

    @KafkaListener(
            topics = "account.credited",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "accountCreditedKafkaListenerContainerFactory"
    )
    public void consume(AccountCreditedEvent event) {
        accountEventHandlerService.handleAccountCredited(
                event.transferId(),
                event.accountId(),
                event.customerId(),
                event.currency(),
                event.balance()
        );
    }
}