package com.banking.account.infrastructure.messaging.event;

import com.banking.account.domain.model.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountDebitedEvent(
        UUID eventId,
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