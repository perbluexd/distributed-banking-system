package com.banking.auth.domain.model;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class  User {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final Set<Role> roles;
    private final Instant createdAt;

    public User(UUID id,
                String email,
                String passwordHash,
                Set<Role> roles,
                Instant createdAt) {

        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.email = Objects.requireNonNull(email, "email cannot be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash cannot be null");
        this.roles = Collections.unmodifiableSet(new HashSet<>(roles));
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
