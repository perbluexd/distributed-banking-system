package com.banking.transaction.infrastructure.messaging.event;

import java.time.Instant;
import java.util.UUID;

public record AccountActivatedEvent(
        UUID accountId,
        UUID customerId,
        String accountNumber,
        Instant occurredAt
) {
}