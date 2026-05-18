package com.banking.notification.api.dto;

import com.banking.notification.domain.model.NotificationChannel;
import com.banking.notification.domain.model.NotificationStatus;
import com.banking.notification.domain.model.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID transferId,
        NotificationType type,
        NotificationChannel channel,
        String recipientEmail,
        NotificationStatus status,
        int attemptCount,
        String errorMessage,
        Instant createdAt,
        Instant sentAt
) {
}