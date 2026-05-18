package com.banking.notification.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationIdTest {

    @Test
    void shouldGenerateNotificationId() {
        NotificationId id = NotificationId.generate();

        assertNotNull(id);
        assertNotNull(id.value());
    }

    @Test
    void shouldCreateNotificationIdFromUuid() {
        UUID uuid = UUID.randomUUID();

        NotificationId id = NotificationId.of(uuid);

        assertEquals(uuid, id.value());
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> new NotificationId(null)
        );
    }
}