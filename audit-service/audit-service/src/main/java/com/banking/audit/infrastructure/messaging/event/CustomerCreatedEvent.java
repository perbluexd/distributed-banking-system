package com.banking.audit.infrastructure.messaging.event;

import java.time.Instant;
import java.util.UUID;

public record CustomerCreatedEvent(
        UUID eventId,
        UUID customerId,
        UUID userId,
        String email,
        String status,
        Instant occurredAt
) {
}