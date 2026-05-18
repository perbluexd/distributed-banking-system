package com.banking.notification.api.mapper;

import com.banking.notification.api.dto.NotificationResponse;
import com.banking.notification.domain.model.*;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationApiMapperTest {

    @Test
    void shouldMapDomainToResponse() {
        Notification notification = Notification.create(
                UUID.randomUUID(),
                NotificationType.TRANSFER_COMPLETED,
                NotificationChannel.EMAIL,
                new Recipient("customer@example.com")
        );

        NotificationResponse response =
                NotificationApiMapper.toResponse(notification);

        assertEquals(notification.getId().value(), response.id());
        assertEquals(notification.getTransferId(), response.transferId());
        assertEquals(notification.getType(), response.type());
        assertEquals(notification.getChannel(), response.channel());
        assertEquals(notification.getRecipient().email(), response.recipientEmail());
        assertEquals(notification.getStatus(), response.status());
        assertEquals(notification.getAttemptCount(), response.attemptCount());
        assertEquals(notification.getErrorMessage(), response.errorMessage());
        assertEquals(notification.getCreatedAt(), response.createdAt());
        assertEquals(notification.getSentAt(), response.sentAt());
    }
}