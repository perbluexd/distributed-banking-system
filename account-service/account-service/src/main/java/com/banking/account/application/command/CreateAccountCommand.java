package com.banking.account.application.command;

import com.banking.account.domain.model.AccountType;
import com.banking.account.domain.model.Currency;

import java.util.UUID;

public record CreateAccountCommand(
        UUID customerId,
        AccountType accountType,
        Currency currency
) {
}