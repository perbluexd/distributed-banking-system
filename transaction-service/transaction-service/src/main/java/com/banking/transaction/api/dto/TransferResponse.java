package com.banking.transaction.api.dto;

import com.banking.transaction.domain.model.Currency;
import com.banking.transaction.domain.model.TransferStatus;
import com.banking.transaction.domain.model.TransferType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferResponse(
        UUID transferId,
        UUID sourceAccountId,
        UUID targetAccountId,
        BigDecimal amount,
        Currency currency,
        TransferType type,
        TransferStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}