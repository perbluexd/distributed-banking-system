package com.banking.audit.infrastructure.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountCreditedEvent(
        UUID eventId,
        UUID transferId,
        UUID accountId,
        UUID customerId,
        String accountNumber,
        BigDecimal amount,
        BigDecimal balance,
        String currency,
        Instant occurredAt
) {
}