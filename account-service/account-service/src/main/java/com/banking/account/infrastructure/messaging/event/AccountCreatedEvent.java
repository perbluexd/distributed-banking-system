package com.banking.account.infrastructure.messaging.event;

import com.banking.account.domain.model.AccountType;
import com.banking.account.domain.model.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountCreatedEvent(
        UUID eventId,
        UUID accountId,
        UUID customerId,
        String accountNumber,
        AccountType accountType,
        BigDecimal balance,
        Currency currency,
        Instant occurredAt
) {
}