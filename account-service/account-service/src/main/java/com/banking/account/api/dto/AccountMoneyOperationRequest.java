package com.banking.account.api.dto;

import com.banking.account.domain.model.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountMoneyOperationRequest(
        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @NotNull
        Currency currency,

        @NotNull
        UUID transferId
) {
}