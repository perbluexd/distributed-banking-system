package com.banking.auth.api.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class UserResponse {

    private UUID id;
    private String email;
    private Set<String> roles;
    private Instant createdAt;

    public UserResponse(UUID id, String email, Set<String> roles, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.roles = roles;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
