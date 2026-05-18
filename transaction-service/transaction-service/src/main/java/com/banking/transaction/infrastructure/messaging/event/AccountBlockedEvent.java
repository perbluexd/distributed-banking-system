package com.banking.transaction.infrastructure.messaging.event;

import java.time.Instant;
import java.util.UUID;

public record AccountBlockedEvent(
        UUID accountId,
        UUID customerId,
        String accountNumber,
        Instant occurredAt
) {
}