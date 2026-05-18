package com.banking.notification.infrastructure.messaging.event;

import java.time.Instant;
import java.util.UUID;

public record CustomerCreatedEvent(
        UUID customerId,
        UUID userId,
        String email,
        String status,
        Instant occurredAt
) {
}