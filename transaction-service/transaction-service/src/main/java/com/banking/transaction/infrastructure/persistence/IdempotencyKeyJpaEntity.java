package com.banking.transaction.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKeyJpaEntity {

    @Id
    @Column(name = "idempotency_key")
    private String idempotencyKey;

    @Column(name = "transfer_id", nullable = false)
    private UUID transferId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected IdempotencyKeyJpaEntity() {
    }

    public IdempotencyKeyJpaEntity(
            String idempotencyKey,
            UUID transferId,
            Instant createdAt
    ) {
        this.idempotencyKey = idempotencyKey;
        this.transferId = transferId;
        this.createdAt = createdAt;
    }

    public String getIdempotencyKey() { return idempotencyKey; }
    public UUID getTransferId() { return transferId; }
    public Instant getCreatedAt() { return createdAt; }
}