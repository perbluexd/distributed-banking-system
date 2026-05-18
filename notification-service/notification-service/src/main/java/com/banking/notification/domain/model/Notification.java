package com.banking.notification.domain.model;

import com.banking.notification.domain.exception.InvalidNotificationStateException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Notification {

    private final NotificationId id;
    private final UUID transferId;
    private final NotificationType type;
    private final NotificationChannel channel;
    private final Recipient recipient;

    private NotificationStatus status;
    private int attemptCount;
    private String errorMessage;

    private final Instant createdAt;
    private Instant sentAt;

    private Notification(
            NotificationId id,
            UUID transferId,
            NotificationType type,
            NotificationChannel channel,
            Recipient recipient,
            NotificationStatus status,
            int attemptCount,
            String errorMessage,
            Instant createdAt,
            Instant sentAt
    ) {
        this.id = Objects.requireNonNull(id);
        this.transferId = Objects.requireNonNull(transferId);
        this.type = Objects.requireNonNull(type);
        this.channel = Objects.requireNonNull(channel);
        this.recipient = Objects.requireNonNull(recipient);
        this.status = Objects.requireNonNull(status);
        this.attemptCount = attemptCount;
        this.errorMessage = errorMessage;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.sentAt = sentAt;
    }

    public static Notification create(
            UUID transferId,
            NotificationType type,
            NotificationChannel channel,
            Recipient recipient
    ) {
        return new Notification(
                NotificationId.generate(),
                transferId,
                type,
                channel,
                recipient,
                NotificationStatus.PENDING,
                0,
                null,
                Instant.now(),
                null
        );
    }

    public static Notification restore(
            NotificationId id,
            UUID transferId,
            NotificationType type,
            NotificationChannel channel,
            Recipient recipient,
            NotificationStatus status,
            int attemptCount,
            String errorMessage,
            Instant createdAt,
            Instant sentAt
    ) {
        return new Notification(
                id,
                transferId,
                type,
                channel,
                recipient,
                status,
                attemptCount,
                errorMessage,
                createdAt,
                sentAt
        );
    }

    public void markAsSent() {
        if (status == NotificationStatus.SENT) {
            throw new InvalidNotificationStateException(
                    "Notification is already marked as SENT"
            );
        }

        this.status = NotificationStatus.SENT;
        this.sentAt = Instant.now();
        this.errorMessage = null;
        this.attemptCount++;
    }

    public void markAsFailed(String errorMessage) {
        if (errorMessage == null || errorMessage.isBlank()) {
            throw new IllegalArgumentException(
                    "Error message cannot be null or blank"
            );
        }

        this.status = NotificationStatus.FAILED;
        this.errorMessage = errorMessage;
        this.attemptCount++;
    }

    public NotificationId getId() {
        return id;
    }

    public UUID getTransferId() {
        return transferId;
    }

    public NotificationType getType() {
        return type;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getSentAt() {
        return sentAt;
    }
}