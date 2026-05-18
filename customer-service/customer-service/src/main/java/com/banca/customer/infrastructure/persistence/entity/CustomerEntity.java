package com.banca.customer.infrastructure.persistence.entity;

import com.banca.customer.domain.model.CustomerStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class CustomerEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "dni", unique = true)
    private String dni;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CustomerStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CustomerEntity() {
    }

    public CustomerEntity(UUID id,
                          UUID userId,
                          String dni,
                          String fullName,
                          String email,
                          CustomerStatus status,
                          Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.dni = dni;
        this.fullName = fullName;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getDni() { return dni; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public CustomerStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}