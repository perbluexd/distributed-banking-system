package com.banca.customer.api.dto;

import com.banca.customer.domain.model.CustomerStatus;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        UUID userId,
        String dni,
        String fullName,
        String email,
        CustomerStatus status,
        Instant createdAt
) {
}