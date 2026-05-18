package com.banking.notification.infrastructure.messaging.consumer;

import com.banking.notification.application.service.CustomerSnapshotService;
import com.banking.notification.infrastructure.messaging.event.CustomerCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(CustomerCreatedConsumer.class);

    private final CustomerSnapshotService customerSnapshotService;

    public CustomerCreatedConsumer(CustomerSnapshotService customerSnapshotService) {
        this.customerSnapshotService = customerSnapshotService;
    }

    @KafkaListener(
            topics = "customer.created",
            groupId = "notification-service",
            containerFactory = "customerCreatedKafkaListenerContainerFactory"
    )
    public void consume(CustomerCreatedEvent event) {

        log.info(
                "[NOTIFICATION] CustomerCreatedEvent received. customerId={}, userId={}, email={}, status={}",
                event.customerId(),
                event.userId(),
                event.email(),
                event.status()
        );

        customerSnapshotService.handleCustomerCreated(event);

        log.info(
                "[NOTIFICATION] CustomerCreatedEvent processed successfully. customerId={}, userId={}",
                event.customerId(),
                event.userId()
        );
    }
}