package com.banking.notification.infrastructure.persistence.mapper;

import com.banking.notification.domain.model.*;
import com.banking.notification.infrastructure.persistence.entity.NotificationJpaEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationPersistenceMapperTest {

    @Test
    void shouldMapDomainToEntity() {
        Notification notification = Notification.create(
                UUID.randomUUID(),
                NotificationType.TRANSFER_COMPLETED,
                NotificationChannel.EMAIL,
                new Recipient("test@example.com")
        );

        NotificationJpaEntity entity =
                NotificationPersistenceMapper.toEntity(notification);

        assertEquals(notification.getId().value(), entity.getId());
        assertEquals(notification.getTransferId(), entity.getTransferId());
        assertEquals(notification.getType(), entity.getType());
        assertEquals(notification.getChannel(), entity.getChannel());
        assertEquals(notification.getRecipient().email(), entity.getRecipientEmail());
        assertEquals(notification.getStatus(), entity.getStatus());
        assertEquals(notification.getAttemptCount(), entity.getAttemptCount());
        assertEquals(notification.getErrorMessage(), entity.getErrorMessage());
        assertEquals(notification.getCreatedAt(), entity.getCreatedAt());
        assertEquals(notification.getSentAt(), entity.getSentAt());
    }

    @Test
    void shouldMapEntityToDomain() {
        Notification notification = Notification.create(
                UUID.randomUUID(),
                NotificationType.TRANSFER_COMPLETED,
                NotificationChannel.EMAIL,
                new Recipient("test@example.com")
        );

        NotificationJpaEntity entity =
                NotificationPersistenceMapper.toEntity(notification);

        Notification result =
                NotificationPersistenceMapper.toDomain(entity);

        assertEquals(notification.getId(), result.getId());
        assertEquals(notification.getTransferId(), result.getTransferId());
        assertEquals(notification.getType(), result.getType());
        assertEquals(notification.getChannel(), result.getChannel());
        assertEquals(notification.getRecipient().email(), result.getRecipient().email());
        assertEquals(notification.getStatus(), result.getStatus());
        assertEquals(notification.getAttemptCount(), result.getAttemptCount());
    }
}