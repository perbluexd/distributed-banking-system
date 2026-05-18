package com.banking.transaction.infrastructure.messaging.event;

import com.banking.transaction.domain.model.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountCreatedEvent(
        UUID accountId,
        UUID customerId,
        String accountNumber,
        String accountType,
        BigDecimal balance,
        Currency currency,
        Instant occurredAt
) {
}