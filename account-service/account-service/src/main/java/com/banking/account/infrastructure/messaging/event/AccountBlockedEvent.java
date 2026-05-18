package com.banking.account.infrastructure.messaging.event;

import java.time.Instant;
import java.util.UUID;
public record AccountBlockedEvent(
        UUID eventId,
        UUID accountId,
        UUID customerId,
        String accountNumber,
        Instant occurredAt
) {
}