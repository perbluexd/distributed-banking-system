package com.banking.notification.infrastructure.persistence.mapper;

import com.banking.notification.domain.model.Notification;
import com.banking.notification.domain.model.NotificationId;
import com.banking.notification.domain.model.Recipient;
import com.banking.notification.infrastructure.persistence.entity.NotificationJpaEntity;

public class NotificationPersistenceMapper {

    private NotificationPersistenceMapper() {
    }

    public static NotificationJpaEntity toEntity(Notification notification) {
        return new NotificationJpaEntity(
                notification.getId().value(),
                notification.getTransferId(),
                notification.getType(),
                notification.getChannel(),
                notification.getRecipient().email(),
                notification.getStatus(),
                notification.getAttemptCount(),
                notification.getErrorMessage(),
                notification.getCreatedAt(),
                notification.getSentAt()
        );
    }

    public static Notification toDomain(NotificationJpaEntity entity) {
        return Notification.restore(
                NotificationId.of(entity.getId()),
                entity.getTransferId(),
                entity.getType(),
                entity.getChannel(),
                new Recipient(entity.getRecipientEmail()),
                entity.getStatus(),
                entity.getAttemptCount(),
                entity.getErrorMessage(),
                entity.getCreatedAt(),
                entity.getSentAt()
        );
    }
}