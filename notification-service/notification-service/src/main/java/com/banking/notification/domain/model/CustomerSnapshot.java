package com.banking.notification.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class CustomerSnapshot {

    private final CustomerId customerId;
    private final UUID userId;
    private String email;
    private String status;
    private final Instant createdAt;
    private Instant updatedAt;

    private CustomerSnapshot(
            CustomerId customerId,
            UUID userId,
            String email,
            String status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.customerId = Objects.requireNonNull(customerId);
        this.userId = Objects.requireNonNull(userId);
        this.email = validateEmail(email);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = updatedAt;
    }

    public static CustomerSnapshot create(
            CustomerId customerId,
            UUID userId,
            String email,
            String status
    ) {
        Instant now = Instant.now();

        return new CustomerSnapshot(
                customerId,
                userId,
                email,
                status,
                now,
                null
        );
    }

    public static CustomerSnapshot restore(
            CustomerId customerId,
            UUID userId,
            String email,
            String status,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new CustomerSnapshot(
                customerId,
                userId,
                email,
                status,
                createdAt,
                updatedAt
        );
    }

    public void update(String email, String status) {
        this.email = validateEmail(email);
        this.status = Objects.requireNonNull(status);
        this.updatedAt = Instant.now();
    }

    private static String validateEmail(String email) {
        Objects.requireNonNull(email, "Customer email cannot be null");

        if (email.isBlank()) {
            throw new IllegalArgumentException("Customer email cannot be blank");
        }

        return email;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}