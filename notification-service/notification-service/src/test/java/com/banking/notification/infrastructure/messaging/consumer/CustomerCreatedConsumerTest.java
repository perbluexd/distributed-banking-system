package com.banking.notification.infrastructure.messaging.consumer;

import com.banking.notification.application.service.CustomerSnapshotService;
import com.banking.notification.infrastructure.messaging.event.CustomerCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;

class CustomerCreatedConsumerTest {

    private CustomerSnapshotService customerSnapshotService;
    private CustomerCreatedConsumer consumer;

    @BeforeEach
    void setUp() {
        customerSnapshotService = mock(CustomerSnapshotService.class);
        consumer = new CustomerCreatedConsumer(customerSnapshotService);
    }

    @Test
    void shouldConsumeCustomerCreatedEvent() {
        CustomerCreatedEvent event = new CustomerCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "customer@example.com",
                "ACTIVE",
                Instant.now()
        );

        consumer.consume(event);

        verify(customerSnapshotService).handleCustomerCreated(event);
    }
}