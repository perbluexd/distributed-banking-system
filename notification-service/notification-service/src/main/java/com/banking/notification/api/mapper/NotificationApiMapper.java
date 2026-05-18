package com.banking.notification.api.mapper;

import com.banking.notification.api.dto.NotificationResponse;
import com.banking.notification.domain.model.Notification;

public class NotificationApiMapper {

    private NotificationApiMapper() {
    }

    public static NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
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
}