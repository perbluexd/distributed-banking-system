package com.banking.auth.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_refresh_tokens_user_id", columnList = "userId")
})
public class RefreshTokenEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 64) // sha256 hex = 64 chars
    private String tokenHash;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    @Column(nullable = false)
    private boolean used;

    @Column(nullable = false)
    private Instant createdAt;

    protected RefreshTokenEntity() {}

    public RefreshTokenEntity(UUID id,
                              String tokenHash,
                              UUID userId,
                              Instant expiresAt,
                              boolean revoked,
                              boolean used,
                              Instant createdAt) {
        this.id = id;
        this.tokenHash = tokenHash;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
        this.used = used;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public String getTokenHash() { return tokenHash; }
    public UUID getUserId() { return userId; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isRevoked() { return revoked; }
    public boolean isUsed() { return used; }
    public Instant getCreatedAt() { return createdAt; }
}
