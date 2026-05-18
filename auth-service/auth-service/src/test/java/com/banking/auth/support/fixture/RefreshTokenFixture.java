package com.banking.auth.support.fixture;

import com.banking.auth.domain.model.RefreshToken;

import java.time.Instant;
import java.util.UUID;

public final class  RefreshTokenFixture {

    private RefreshTokenFixture() {
    }

    public static RefreshToken anyRefreshToken() {
        return new RefreshToken(
                UUID.randomUUID(),
                "token-hash",
                UUID.randomUUID(),
                Instant.parse("2026-04-13T10:00:00Z"),
                false,
                false,
                Instant.parse("2026-03-13T10:00:00Z")
        );
    }

    public static RefreshToken withUserId(UUID userId) {
        return new RefreshToken(
                UUID.randomUUID(),
                "token-hash",
                userId,
                Instant.parse("2026-04-13T10:00:00Z"),
                false,
                false,
                Instant.parse("2026-03-13T10:00:00Z")
        );
    }

    public static RefreshToken revoked(UUID userId) {
        return new RefreshToken(
                UUID.randomUUID(),
                "token-hash",
                userId,
                Instant.parse("2026-04-13T10:00:00Z"),
                true,
                false,
                Instant.parse("2026-03-13T10:00:00Z")
        );
    }

    public static RefreshToken used(UUID userId) {
        return new RefreshToken(
                UUID.randomUUID(),
                "token-hash",
                userId,
                Instant.parse("2026-04-13T10:00:00Z"),
                false,
                true,
                Instant.parse("2026-03-13T10:00:00Z")
        );
    }

    public static RefreshToken expired(UUID userId) {
        return new RefreshToken(
                UUID.randomUUID(),
                "token-hash",
                userId,
                Instant.parse("2026-03-13T09:00:00Z"),
                false,
                false,
                Instant.parse("2026-03-13T08:00:00Z")
        );
    }

    public static RefreshToken full(UUID id,
                                    String tokenHash,
                                    UUID userId,
                                    Instant expiresAt,
                                    boolean revoked,
                                    boolean used,
                                    Instant createdAt) {
        return new RefreshToken(
                id,
                tokenHash,
                userId,
                expiresAt,
                revoked,
                used,
                createdAt
        );
    }
}