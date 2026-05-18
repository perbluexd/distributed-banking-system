package com.banking.account.infrastructure.messaging.consumer;

import com.banking.account.application.service.CustomerEventHandlerService;
import com.banking.account.infrastructure.messaging.event.CustomerCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(CustomerCreatedConsumer.class);

    private final CustomerEventHandlerService customerEventHandlerService;

    public CustomerCreatedConsumer(CustomerEventHandlerService customerEventHandlerService) {
        this.customerEventHandlerService = customerEventHandlerService;
    }

    @KafkaListener(
            topics = "customer.created",
            groupId = "account-service"
    )
    public void consume(CustomerCreatedEvent event) {

        log.info(
                "[ACCOUNT] CustomerCreatedEvent received. eventType={}, topic={}, customerId={}, userId={}, email={}",
                "CustomerCreatedEvent",
                "customer.created",
                event.customerId(),
                event.userId(),
                event.email()
        );

        customerEventHandlerService.handleCustomerCreated(event);

        log.info(
                "[ACCOUNT] CustomerCreatedEvent processed successfully. customerId={}, userId={}",
                event.customerId(),
                event.userId()
        );
    }
}