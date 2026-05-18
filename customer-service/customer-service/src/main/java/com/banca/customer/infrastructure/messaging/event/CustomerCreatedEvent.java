package com.banca.customer.infrastructure.messaging.event;

import com.banca.customer.domain.model.CustomerStatus;

import java.time.Instant;
import java.util.UUID;

public record CustomerCreatedEvent(
        UUID eventId,
        UUID customerId,
        UUID userId,
        String email,
        CustomerStatus status,
        Instant occurredAt
) {
}