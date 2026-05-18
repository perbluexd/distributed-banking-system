package com.banking.account.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customer_snapshots")
public class CustomerSnapshotJpaEntity {

    @Id
    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_event_at", nullable = false)
    private Instant lastEventAt;

    protected CustomerSnapshotJpaEntity() {
    }

    public CustomerSnapshotJpaEntity(
            UUID customerId,
            UUID userId,
            String email,
            String status,
            Instant createdAt,
            Instant lastEventAt
    ) {
        this.customerId = customerId;
        this.userId = userId;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
        this.lastEventAt = lastEventAt;
    }

    public UUID getCustomerId() { return customerId; }
    public UUID getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastEventAt() { return lastEventAt; }
}