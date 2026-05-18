package com.banking.notification.infrastructure.persistence.entity;

import com.banking.notification.domain.model.NotificationChannel;
import com.banking.notification.domain.model.NotificationStatus;
import com.banking.notification.domain.model.NotificationType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "notifications",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_notifications_transfer_type",
                        columnNames = {"transfer_id", "type"}
                )
        }
)
public class NotificationJpaEntity {

    @Id
    private UUID id;

    @Column(name = "transfer_id", nullable = false)
    private UUID transferId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    protected NotificationJpaEntity() {
    }

    public NotificationJpaEntity(
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
        this.id = id;
        this.transferId = transferId;
        this.type = type;
        this.channel = channel;
        this.recipientEmail = recipientEmail;
        this.status = status;
        this.attemptCount = attemptCount;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
    }

    public UUID getId() {
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

    public String getRecipientEmail() {
        return recipientEmail;
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