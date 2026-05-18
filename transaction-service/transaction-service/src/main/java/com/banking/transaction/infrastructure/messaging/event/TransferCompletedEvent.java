package com.banking.transaction.infrastructure.messaging.event;

import com.banking.transaction.domain.model.Currency;
import com.banking.transaction.domain.model.TransferStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferCompletedEvent(
        UUID eventId,
        UUID transferId,
        UUID customerId,
        UUID sourceAccountId,
        UUID targetAccountId,
        BigDecimal amount,
        Currency currency,
        TransferStatus status,
        Instant occurredAt
) {
}