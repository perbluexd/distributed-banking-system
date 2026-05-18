package com.banca.customer.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Customer {

    private final UUID id;
    private final UUID userId;
    private final String dni;
    private final String fullName;
    private final String email;
    private CustomerStatus status;
    private final Instant createdAt;

    public Customer(UUID id,
                    UUID userId,
                    String dni,
                    String fullName,
                    String email,
                    CustomerStatus status,
                    Instant createdAt) {

        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email cannot be empty");
        }

        this.id = id;
        this.userId = userId;
        this.dni = dni;
        this.fullName = fullName;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Customer create(UUID userId, String email) {
        return new Customer(
                UUID.randomUUID(),
                userId,
                null,
                null,
                email,
                CustomerStatus.PENDING,
                Instant.now()
        );
    }

    public void activate() {
        this.status = CustomerStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = CustomerStatus.INACTIVE;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getDni() { return dni; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public CustomerStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}