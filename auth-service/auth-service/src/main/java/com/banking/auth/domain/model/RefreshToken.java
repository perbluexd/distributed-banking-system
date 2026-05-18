package com.banking.auth.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class RefreshToken {

    private final UUID id;
    private final String tokenHash;
    private final UUID userId;
    private final Instant expiresAt;
    private final boolean revoked;
    private final boolean used;
    private final Instant createdAt;

    public RefreshToken(UUID id,
                        String tokenHash,
                        UUID userId,
                        Instant expiresAt,
                        boolean revoked,
                        boolean used,
                        Instant createdAt) {

        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.tokenHash = Objects.requireNonNull(tokenHash, "tokenHash cannot be null");
        this.userId = Objects.requireNonNull(userId, "userId cannot be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
        this.revoked = revoked;
        this.used = used;
    }

    public UUID getId() {
        return id;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public UUID getUserId() {
        return userId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public boolean isUsed() {
        return used;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
