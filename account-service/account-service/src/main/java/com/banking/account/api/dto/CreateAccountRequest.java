package com.banking.account.api.dto;

import com.banking.account.domain.model.AccountType;
import com.banking.account.domain.model.Currency;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateAccountRequest(

        @NotNull(message = "Customer id is required")
        UUID customerId,

        @NotNull(message = "Account type is required")
        AccountType accountType,

        @NotNull(message = "Currency is required")
        Currency currency
) {
}