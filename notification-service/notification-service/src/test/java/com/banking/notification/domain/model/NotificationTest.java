package com.banking.notification.domain.model;

import com.banking.notification.domain.exception.InvalidNotificationStateException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void shouldCreateNotificationInPendingState() {
        Notification notification = Notification.create(
                UUID.randomUUID(),
                NotificationType.TRANSFER_COMPLETED,
                NotificationChannel.EMAIL,
                new Recipient("test@example.com")
        );

        assertNotNull(notification.getId());
        assertEquals(NotificationStatus.PENDING, notification.getStatus());
        assertEquals(0, notification.getAttemptCount());
        assertNull(notification.getErrorMessage());
        assertNull(notification.getSentAt());
        assertNotNull(notification.getCreatedAt());
    }

    @Test
    void shouldMarkNotificationAsSent() {
        Notification notification = Notification.create(
                UUID.randomUUID(),
                NotificationType.TRANSFER_COMPLETED,
                NotificationChannel.EMAIL,
                new Recipient("test@example.com")
        );

        notification.markAsSent();

        assertEquals(NotificationStatus.SENT, notification.getStatus());
        assertEquals(1, notification.getAttemptCount());
        assertNull(notification.getErrorMessage());
        assertNotNull(notification.getSentAt());
    }

    @Test
    void shouldThrowExceptionWhenMarkingAlreadySentNotification() {
        Notification notification = Notification.create(
                UUID.randomUUID(),
                NotificationType.TRANSFER_COMPLETED,
                NotificationChannel.EMAIL,
                new Recipient("test@example.com")
        );

        notification.markAsSent();

        assertThrows(
                InvalidNotificationStateException.class,
                notification::markAsSent
        );
    }

    @Test
    void shouldMarkNotificationAsFailed() {
        Notification notification = Notification.create(
                UUID.randomUUID(),
                NotificationType.TRANSFER_COMPLETED,
                NotificationChannel.EMAIL,
                new Recipient("test@example.com")
        );

        notification.markAsFailed("SMTP connection failed");

        assertEquals(NotificationStatus.FAILED, notification.getStatus());
        assertEquals(1, notification.getAttemptCount());
        assertEquals("SMTP connection failed", notification.getErrorMessage());
        assertNull(notification.getSentAt());
    }

    @Test
    void shouldThrowExceptionWhenErrorMessageIsNull() {
        Notification notification = Notification.create(
                UUID.randomUUID(),
                NotificationType.TRANSFER_COMPLETED,
                NotificationChannel.EMAIL,
                new Recipient("test@example.com")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> notification.markAsFailed(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenErrorMessageIsBlank() {
        Notification notification = Notification.create(
                UUID.randomUUID(),
                NotificationType.TRANSFER_COMPLETED,
                NotificationChannel.EMAIL,
                new Recipient("test@example.com")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> notification.markAsFailed("   ")
        );
    }
}