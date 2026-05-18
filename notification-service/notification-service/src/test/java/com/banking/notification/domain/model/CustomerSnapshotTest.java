package com.banking.notification.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerSnapshotTest {

    @Test
    void shouldCreateCustomerSnapshot() {
        CustomerSnapshot snapshot = CustomerSnapshot.create(
                CustomerId.of(UUID.randomUUID()),
                UUID.randomUUID(),
                "customer@example.com",
                "ACTIVE"
        );

        assertNotNull(snapshot.getCustomerId());
        assertNotNull(snapshot.getUserId());
        assertEquals("customer@example.com", snapshot.getEmail());
        assertEquals("ACTIVE", snapshot.getStatus());
        assertNotNull(snapshot.getCreatedAt());
        assertNull(snapshot.getUpdatedAt());
    }

    @Test
    void shouldUpdateEmailStatusAndUpdatedAt() {
        CustomerSnapshot snapshot = CustomerSnapshot.create(
                CustomerId.of(UUID.randomUUID()),
                UUID.randomUUID(),
                "old@example.com",
                "ACTIVE"
        );

        snapshot.update("new@example.com", "INACTIVE");

        assertEquals("new@example.com", snapshot.getEmail());
        assertEquals("INACTIVE", snapshot.getStatus());
        assertNotNull(snapshot.getUpdatedAt());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> CustomerSnapshot.create(
                        CustomerId.of(UUID.randomUUID()),
                        UUID.randomUUID(),
                        null,
                        "ACTIVE"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> CustomerSnapshot.create(
                        CustomerId.of(UUID.randomUUID()),
                        UUID.randomUUID(),
                        "   ",
                        "ACTIVE"
                )
        );
    }

    @Test
    void shouldRestoreCustomerSnapshot() {
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();

        CustomerSnapshot snapshot = CustomerSnapshot.restore(
                CustomerId.of(UUID.randomUUID()),
                UUID.randomUUID(),
                "customer@example.com",
                "ACTIVE",
                createdAt,
                updatedAt
        );

        assertEquals(createdAt, snapshot.getCreatedAt());
        assertEquals(updatedAt, snapshot.getUpdatedAt());
    }
}