package com.banking.transaction.domain.model;

import java.time.Instant;

public class Transfer {

    private final TransferId id;
    private final AccountId sourceAccountId;
    private final AccountId targetAccountId;
    private final Money amount;
    private final TransferType type;
    private final IdempotencyKey idempotencyKey;
    private TransferStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    private Transfer(
            TransferId id,
            AccountId sourceAccountId,
            AccountId targetAccountId,
            Money amount,
            TransferType type,
            IdempotencyKey idempotencyKey,
            TransferStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.type = type;
        this.idempotencyKey = idempotencyKey;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        validate();
    }

    public static Transfer create(
            AccountId sourceAccountId,
            AccountId targetAccountId,
            Money amount,
            TransferType type,
            IdempotencyKey idempotencyKey
    ) {
        if (sourceAccountId.equals(targetAccountId)) {
            throw new IllegalArgumentException("Source and target accounts cannot be the same");
        }

        Instant now = Instant.now();

        return new Transfer(
                TransferId.newId(),
                sourceAccountId,
                targetAccountId,
                amount,
                type,
                idempotencyKey,
                TransferStatus.PENDING,
                now,
                now
        );
    }

    public static Transfer restore(
            TransferId id,
            AccountId sourceAccountId,
            AccountId targetAccountId,
            Money amount,
            TransferType type,
            IdempotencyKey idempotencyKey,
            TransferStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Transfer(
                id,
                sourceAccountId,
                targetAccountId,
                amount,
                type,
                idempotencyKey,
                status,
                createdAt,
                updatedAt
        );
    }

    public void markCompleted() {
        if (this.status != TransferStatus.PENDING) {
            throw new IllegalStateException("Only pending transfers can be completed");
        }

        this.status = TransferStatus.COMPLETED;
        this.updatedAt = Instant.now();
    }

    public void markFailed() {
        if (this.status != TransferStatus.PENDING) {
            throw new IllegalStateException("Only pending transfers can be failed");
        }

        this.status = TransferStatus.FAILED;
        this.updatedAt = Instant.now();
    }

    private void validate() {
        if (id == null) throw new IllegalArgumentException("TransferId required");
        if (sourceAccountId == null) throw new IllegalArgumentException("SourceAccountId required");
        if (targetAccountId == null) throw new IllegalArgumentException("TargetAccountId required");
        if (amount == null) throw new IllegalArgumentException("Amount required");
        if (type == null) throw new IllegalArgumentException("TransferType required");
        if (idempotencyKey == null) throw new IllegalArgumentException("IdempotencyKey required");
        if (status == null) throw new IllegalArgumentException("Status required");
        if (createdAt == null) throw new IllegalArgumentException("CreatedAt required");
        if (updatedAt == null) throw new IllegalArgumentException("UpdatedAt required");
    }

    public TransferId getId() {
        return id;
    }

    public AccountId getSourceAccountId() {
        return sourceAccountId;
    }

    public AccountId getTargetAccountId() {
        return targetAccountId;
    }

    public Money getAmount() {
        return amount;
    }

    public TransferType getType() {
        return type;
    }

    public IdempotencyKey getIdempotencyKey() {
        return idempotencyKey;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}