package com.banking.notification.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecipientTest {

    @Test
    void shouldCreateRecipient() {
        Recipient recipient = new Recipient("test@example.com");

        assertEquals("test@example.com", recipient.email());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> new Recipient(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Recipient("   ")
        );
    }
}