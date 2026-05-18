package com.banking.account.domain.model;

import java.time.Instant;
import java.util.UUID;

public class CustomerSnapshot {

    private final UUID customerId;
    private final UUID userId;
    private final String email;
    private final String status;
    private final Instant createdAt;
    private final Instant lastEventAt;

    public CustomerSnapshot(
            UUID customerId,
            UUID userId,
            String email,
            String status,
            Instant createdAt,
            Instant lastEventAt
    ) {
        if (customerId == null) throw new IllegalArgumentException("customerId cannot be null");
        if (userId == null) throw new IllegalArgumentException("userId cannot be null");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email cannot be blank");
        if (status == null || status.isBlank()) throw new IllegalArgumentException("status cannot be blank");
        if (createdAt == null) throw new IllegalArgumentException("createdAt cannot be null");
        if (lastEventAt == null) throw new IllegalArgumentException("lastEventAt cannot be null");

        this.customerId = customerId;
        this.userId = userId;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
        this.lastEventAt = lastEventAt;
    }

    public static CustomerSnapshot create(
            UUID customerId,
            UUID userId,
            String email,
            String status,
            Instant createdAt,
            Instant lastEventAt
    ) {
        return new CustomerSnapshot(
                customerId,
                userId,
                email,
                status,
                createdAt,
                lastEventAt
        );
    }

    public UUID getCustomerId() { return customerId; }
    public UUID getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastEventAt() { return lastEventAt; }
}