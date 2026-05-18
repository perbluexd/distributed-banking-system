package com.banking.account.application.command;

import com.banking.account.domain.model.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record DebitAccountCommand(
        UUID accountId,
        BigDecimal amount,
        Currency currency,
        UUID transferId
) {
}