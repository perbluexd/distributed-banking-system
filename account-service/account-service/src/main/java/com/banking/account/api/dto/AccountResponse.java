package com.banking.account.api.dto;

import com.banking.account.domain.model.AccountStatus;
import com.banking.account.domain.model.AccountType;
import com.banking.account.domain.model.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountResponse(
        UUID accountId,
        UUID customerId,
        String accountNumber,
        AccountType accountType,
        AccountStatus status,
        BigDecimal balance,
        Currency currency,
        Instant createdAt,
        Instant updatedAt
) {
}