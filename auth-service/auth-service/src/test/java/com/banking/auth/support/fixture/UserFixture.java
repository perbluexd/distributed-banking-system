package com.banking.auth.support.fixture;

import com.banking.auth.domain.model.Role;
import com.banking.auth.domain.model.User;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public final class UserFixture {

    private UserFixture() {
    }

    public static User anyUser() {
        return new User(
                UUID.randomUUID(),
                "user@mail.com",
                "hashed-password",
                Set.of(Role.USER),
                Instant.parse("2026-03-13T10:00:00Z")
        );
    }

    public static User withId(UUID userId) {
        return new User(
                userId,
                "user@mail.com",
                "hashed-password",
                Set.of(Role.USER),
                Instant.parse("2026-03-13T10:00:00Z")
        );
    }

    public static User withEmail(String email) {
        return new User(
                UUID.randomUUID(),
                email,
                "hashed-password",
                Set.of(Role.USER),
                Instant.parse("2026-03-13T10:00:00Z")
        );
    }

    public static User withPasswordHash(String passwordHash) {
        return new User(
                UUID.randomUUID(),
                "user@mail.com",
                passwordHash,
                Set.of(Role.USER),
                Instant.parse("2026-03-13T10:00:00Z")
        );
    }

    public static User full(UUID userId,
                            String email,
                            String passwordHash,
                            Set<Role> roles,
                            Instant createdAt) {
        return new User(
                userId,
                email,
                passwordHash,
                roles,
                createdAt
        );
    }
}