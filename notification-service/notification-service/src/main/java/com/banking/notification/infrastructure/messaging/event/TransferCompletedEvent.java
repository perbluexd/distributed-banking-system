package com.banking.notification.infrastructure.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferCompletedEvent(
        UUID transferId,
        UUID customerId,
        UUID sourceAccountId,
        UUID targetAccountId,
        BigDecimal amount,
        Object currency,
        Object status,
        Instant occurredAt
) {
}