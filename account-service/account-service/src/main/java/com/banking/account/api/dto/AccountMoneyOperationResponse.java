package com.banking.account.api.dto;

import com.banking.account.domain.model.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountMoneyOperationResponse(
        UUID accountId,
        UUID transferId,
        BigDecimal balance,
        Currency currency,
        String message
) {
}