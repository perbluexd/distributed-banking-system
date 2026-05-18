package com.banking.notification.application.service;

import com.banking.notification.application.error.NotificationAlreadyProcessedException;
import com.banking.notification.application.error.NotificationProcessingException;
import com.banking.notification.application.port.out.CustomerSnapshotRepositoryPort;
import com.banking.notification.application.port.out.EmailSenderPort;
import com.banking.notification.application.port.out.NotificationRepositoryPort;
import com.banking.notification.domain.model.*;
import com.banking.notification.infrastructure.messaging.event.TransferCompletedEvent;
import com.banking.notification.infrastructure.messaging.event.TransferFailedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationApplicationServiceTest {

    private NotificationRepositoryPort notificationRepositoryPort;
    private CustomerSnapshotRepositoryPort customerSnapshotRepositoryPort;
    private EmailSenderPort emailSenderPort;

    private NotificationApplicationService service;

    @BeforeEach
    void setUp() {
        notificationRepositoryPort = mock(NotificationRepositoryPort.class);
        customerSnapshotRepositoryPort = mock(CustomerSnapshotRepositoryPort.class);
        emailSenderPort = mock(EmailSenderPort.class);

        service = new NotificationApplicationService(
                notificationRepositoryPort,
                customerSnapshotRepositoryPort,
                emailSenderPort
        );
    }

    @Test
    void shouldProcessTransferCompletedEventAndSendEmail() {
        UUID transferId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        TransferCompletedEvent event = new TransferCompletedEvent(
                transferId,
                customerId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(100),
                "PEN",
                "COMPLETED",
                Instant.now()
        );

        CustomerSnapshot snapshot = CustomerSnapshot.create(
                CustomerId.of(customerId),
                userId,
                "customer@example.com",
                "ACTIVE"
        );

        when(notificationRepositoryPort.existsByTransferIdAndType(
                transferId,
                NotificationType.TRANSFER_COMPLETED
        )).thenReturn(false);

        when(customerSnapshotRepositoryPort.findByCustomerId(CustomerId.of(customerId)))
                .thenReturn(Optional.of(snapshot));

        service.process(event);

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepositoryPort, times(2)).save(captor.capture());
        verify(emailSenderPort).send(any(Notification.class));

        Notification finalNotification = captor.getAllValues().get(1);

        assertEquals(NotificationStatus.SENT, finalNotification.getStatus());
        assertEquals(NotificationType.TRANSFER_COMPLETED, finalNotification.getType());
        assertEquals(NotificationChannel.EMAIL, finalNotification.getChannel());
        assertEquals("customer@example.com", finalNotification.getRecipient().email());
        assertEquals(1, finalNotification.getAttemptCount());
        assertNull(finalNotification.getErrorMessage());
        assertNotNull(finalNotification.getSentAt());
    }

    @Test
    void shouldProcessTransferFailedEventAndSendEmail() {
        UUID transferId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        TransferFailedEvent event = new TransferFailedEvent(
                transferId,
                customerId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(100),
                "PEN",
                "FAILED",
                "Insufficient funds",
                Instant.now()
        );

        CustomerSnapshot snapshot = CustomerSnapshot.create(
                CustomerId.of(customerId),
                userId,
                "customer@example.com",
                "ACTIVE"
        );

        when(notificationRepositoryPort.existsByTransferIdAndType(
                transferId,
                NotificationType.TRANSFER_FAILED
        )).thenReturn(false);

        when(customerSnapshotRepositoryPort.findByCustomerId(CustomerId.of(customerId)))
                .thenReturn(Optional.of(snapshot));

        service.process(event);

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepositoryPort, times(2)).save(captor.capture());
        verify(emailSenderPort).send(any(Notification.class));

        Notification finalNotification = captor.getAllValues().get(1);

        assertEquals(NotificationStatus.SENT, finalNotification.getStatus());
        assertEquals(NotificationType.TRANSFER_FAILED, finalNotification.getType());
        assertEquals(NotificationChannel.EMAIL, finalNotification.getChannel());
        assertEquals("customer@example.com", finalNotification.getRecipient().email());
        assertEquals(1, finalNotification.getAttemptCount());
        assertNull(finalNotification.getErrorMessage());
        assertNotNull(finalNotification.getSentAt());
    }

    @Test
    void shouldThrowExceptionWhenNotificationAlreadyExists() {
        UUID transferId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        TransferCompletedEvent event = new TransferCompletedEvent(
                transferId,
                customerId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(100),
                "PEN",
                "COMPLETED",
                Instant.now()
        );

        when(notificationRepositoryPort.existsByTransferIdAndType(
                transferId,
                NotificationType.TRANSFER_COMPLETED
        )).thenReturn(true);

        assertThrows(
                NotificationAlreadyProcessedException.class,
                () -> service.process(event)
        );

        verify(customerSnapshotRepositoryPort, never()).findByCustomerId(any());
        verify(notificationRepositoryPort, never()).save(any());
        verify(emailSenderPort, never()).send(any());
    }

    @Test
    void shouldThrowExceptionWhenCustomerSnapshotDoesNotExist() {
        UUID transferId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        TransferCompletedEvent event = new TransferCompletedEvent(
                transferId,
                customerId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(100),
                "PEN",
                "COMPLETED",
                Instant.now()
        );

        when(notificationRepositoryPort.existsByTransferIdAndType(
                transferId,
                NotificationType.TRANSFER_COMPLETED
        )).thenReturn(false);

        when(customerSnapshotRepositoryPort.findByCustomerId(CustomerId.of(customerId)))
                .thenReturn(Optional.empty());

        assertThrows(
                NotificationProcessingException.class,
                () -> service.process(event)
        );

        verify(notificationRepositoryPort, never()).save(any());
        verify(emailSenderPort, never()).send(any());
    }

    @Test
    void shouldSaveNotificationAsFailedWhenEmailSenderFails() {
        UUID transferId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        TransferCompletedEvent event = new TransferCompletedEvent(
                transferId,
                customerId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(100),
                "PEN",
                "COMPLETED",
                Instant.now()
        );

        CustomerSnapshot snapshot = CustomerSnapshot.create(
                CustomerId.of(customerId),
                userId,
                "customer@example.com",
                "ACTIVE"
        );

        when(notificationRepositoryPort.existsByTransferIdAndType(
                transferId,
                NotificationType.TRANSFER_COMPLETED
        )).thenReturn(false);

        when(customerSnapshotRepositoryPort.findByCustomerId(CustomerId.of(customerId)))
                .thenReturn(Optional.of(snapshot));

        doThrow(new RuntimeException("SMTP failed"))
                .when(emailSenderPort)
                .send(any(Notification.class));

        assertThrows(
                NotificationProcessingException.class,
                () -> service.process(event)
        );

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationRepositoryPort, times(2)).save(captor.capture());

        Notification failedNotification = captor.getAllValues().get(1);

        assertEquals(NotificationStatus.FAILED, failedNotification.getStatus());
        assertEquals(1, failedNotification.getAttemptCount());
        assertEquals("SMTP failed", failedNotification.getErrorMessage());
        assertNull(failedNotification.getSentAt());
    }
}