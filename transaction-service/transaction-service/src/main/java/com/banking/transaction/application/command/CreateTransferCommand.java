package com.banking.transaction.application.command;

import com.banking.transaction.domain.model.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransferCommand(
        UUID sourceAccountId,
        UUID targetAccountId,
        BigDecimal amount,
        Currency currency,
        String idempotencyKey
) {
}