package com.banking.audit.infrastructure.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountCreatedEvent(
        UUID eventId,
        UUID accountId,
        UUID customerId,
        String accountNumber,
        String accountType,
        BigDecimal balance,
        String currency,
        Instant occurredAt
) {
}