package com.banking.audit.infrastructure.messaging.event;

import java.time.Instant;
import java.util.UUID;

public record AccountActivatedEvent(
        UUID eventId,
        UUID accountId,
        UUID customerId,
        String accountNumber,
        Instant occurredAt
) {
}