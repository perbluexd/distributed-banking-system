package com.banking.notification.application.service;

import com.banking.notification.application.error.NotificationNotFoundException;
import com.banking.notification.application.port.out.NotificationRepositoryPort;
import com.banking.notification.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetNotificationByIdServiceTest {

    private NotificationRepositoryPort notificationRepositoryPort;
    private GetNotificationByIdService service;

    @BeforeEach
    void setUp() {
        notificationRepositoryPort = mock(NotificationRepositoryPort.class);
        service = new GetNotificationByIdService(notificationRepositoryPort);
    }

    @Test
    void shouldReturnNotificationById() {
        NotificationId notificationId = NotificationId.generate();

        Notification notification = Notification.restore(
                notificationId,
                UUID.randomUUID(),
                NotificationType.TRANSFER_COMPLETED,
                NotificationChannel.EMAIL,
                new Recipient("customer@example.com"),
                NotificationStatus.PENDING,
                0,
                null,
                java.time.Instant.now(),
                null
        );

        when(notificationRepositoryPort.findById(notificationId))
                .thenReturn(Optional.of(notification));

        Notification result = service.getById(notificationId);

        assertEquals(notification, result);
        verify(notificationRepositoryPort).findById(notificationId);
    }

    @Test
    void shouldThrowExceptionWhenNotificationDoesNotExist() {
        NotificationId notificationId = NotificationId.generate();

        when(notificationRepositoryPort.findById(notificationId))
                .thenReturn(Optional.empty());

        assertThrows(
                NotificationNotFoundException.class,
                () -> service.getById(notificationId)
        );

        verify(notificationRepositoryPort).findById(notificationId);
    }
}