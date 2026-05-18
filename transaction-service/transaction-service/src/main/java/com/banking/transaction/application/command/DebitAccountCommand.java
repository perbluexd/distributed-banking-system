package com.banking.transaction.application.command;

import com.banking.transaction.domain.model.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record DebitAccountCommand(
        UUID accountId,
        BigDecimal amount,
        Currency currency,
        UUID transferId
) {
}