package com.banking.notification.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerIdTest {

    @Test
    void shouldCreateCustomerIdFromUuid() {
        UUID uuid = UUID.randomUUID();

        CustomerId customerId = CustomerId.of(uuid);

        assertEquals(uuid, customerId.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> new CustomerId(null)
        );
    }
}