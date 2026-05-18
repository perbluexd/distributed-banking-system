package com.banking.auth.infrastructure.messaging.event;

import java.time.Instant;
import java.util.UUID;

public record UserLoggedInEvent(
        UUID eventId,
        UUID userId,
        String username,
        Instant occurredAt
) {
}