package com.banking.notification.application.service;

import com.banking.notification.application.port.out.CustomerSnapshotRepositoryPort;
import com.banking.notification.domain.model.CustomerId;
import com.banking.notification.domain.model.CustomerSnapshot;
import com.banking.notification.infrastructure.messaging.event.CustomerCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerSnapshotServiceTest {

    private CustomerSnapshotRepositoryPort customerSnapshotRepositoryPort;
    private CustomerSnapshotService service;

    @BeforeEach
    void setUp() {
        customerSnapshotRepositoryPort = mock(CustomerSnapshotRepositoryPort.class);
        service = new CustomerSnapshotService(customerSnapshotRepositoryPort);
    }

    @Test
    void shouldCreateNewCustomerSnapshot() {
        UUID customerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CustomerCreatedEvent event = new CustomerCreatedEvent(
                customerId,
                userId,
                "customer@example.com",
                "ACTIVE",
                Instant.now()
        );

        when(customerSnapshotRepositoryPort.findByCustomerId(CustomerId.of(customerId)))
                .thenReturn(Optional.empty());

        service.handleCustomerCreated(event);

        ArgumentCaptor<CustomerSnapshot> captor =
                ArgumentCaptor.forClass(CustomerSnapshot.class);

        verify(customerSnapshotRepositoryPort).save(captor.capture());

        CustomerSnapshot savedSnapshot = captor.getValue();

        assertEquals(CustomerId.of(customerId), savedSnapshot.getCustomerId());
        assertEquals(userId, savedSnapshot.getUserId());
        assertEquals("customer@example.com", savedSnapshot.getEmail());
        assertEquals("ACTIVE", savedSnapshot.getStatus());
        assertNotNull(savedSnapshot.getCreatedAt());
        assertNull(savedSnapshot.getUpdatedAt());
    }

    @Test
    void shouldUpdateExistingCustomerSnapshot() {
        UUID customerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CustomerSnapshot existingSnapshot = CustomerSnapshot.create(
                CustomerId.of(customerId),
                userId,
                "old@example.com",
                "ACTIVE"
        );

        CustomerCreatedEvent event = new CustomerCreatedEvent(
                customerId,
                userId,
                "new@example.com",
                "INACTIVE",
                Instant.now()
        );

        when(customerSnapshotRepositoryPort.findByCustomerId(CustomerId.of(customerId)))
                .thenReturn(Optional.of(existingSnapshot));

        service.handleCustomerCreated(event);

        ArgumentCaptor<CustomerSnapshot> captor =
                ArgumentCaptor.forClass(CustomerSnapshot.class);

        verify(customerSnapshotRepositoryPort).save(captor.capture());

        CustomerSnapshot updatedSnapshot = captor.getValue();

        assertEquals(CustomerId.of(customerId), updatedSnapshot.getCustomerId());
        assertEquals(userId, updatedSnapshot.getUserId());
        assertEquals("new@example.com", updatedSnapshot.getEmail());
        assertEquals("INACTIVE", updatedSnapshot.getStatus());
        assertNotNull(updatedSnapshot.getUpdatedAt());
    }
}