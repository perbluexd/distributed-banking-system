package com.banking.transaction.infrastructure.messaging.event;

import com.banking.transaction.domain.model.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountDebitedEvent(
        UUID transferId,
        UUID accountId,
        UUID customerId,
        String accountNumber,
        BigDecimal amount,
        BigDecimal balance,
        Currency currency,
        Instant occurredAt
) {
}