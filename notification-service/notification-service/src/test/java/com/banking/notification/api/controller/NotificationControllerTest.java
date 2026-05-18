package com.banking.notification.api.controller;

import com.banking.notification.application.port.in.GetNotificationByIdUseCase;
import com.banking.notification.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationControllerTest {

    private GetNotificationByIdUseCase getNotificationByIdUseCase;
    private NotificationController controller;

    @BeforeEach
    void setUp() {
        getNotificationByIdUseCase = mock(GetNotificationByIdUseCase.class);
        controller = new NotificationController(getNotificationByIdUseCase);
    }

    @Test
    void shouldGetNotificationById() {
        NotificationId notificationId = NotificationId.generate();

        Notification notification = Notification.create(
                UUID.randomUUID(),
                NotificationType.TRANSFER_COMPLETED,
                NotificationChannel.EMAIL,
                new Recipient("customer@example.com")
        );

        when(getNotificationByIdUseCase.getById(NotificationId.of(notificationId.value())))
                .thenReturn(notification);

        var response = controller.getById(notificationId.value());

        assertEquals(notification.getId().value(), response.id());
        assertEquals(notification.getTransferId(), response.transferId());
        assertEquals(notification.getType(), response.type());
        assertEquals(notification.getChannel(), response.channel());
        assertEquals(notification.getRecipient().email(), response.recipientEmail());
        assertEquals(notification.getStatus(), response.status());

        verify(getNotificationByIdUseCase)
                .getById(NotificationId.of(notificationId.value()));
    }
}