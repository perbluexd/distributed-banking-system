package com.banking.audit.domain.model;

import java.util.Objects;
import java.util.UUID;

public record AuditActor(
        UUID actorId,
        String actorType
) {

    public AuditActor {
        Objects.requireNonNull(actorType, "Actor type cannot be null");

        if (actorType.isBlank()) {
            throw new IllegalArgumentException("Actor type cannot be blank");
        }
    }

    public static AuditActor system() {
        return new AuditActor(null, "SYSTEM");
    }

    public static AuditActor user(UUID userId) {
        Objects.requireNonNull(userId, "User id cannot be null");
        return new AuditActor(userId, "USER");
    }
}