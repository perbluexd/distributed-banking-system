package com.banking.audit.api.dto;

import java.time.Instant;
import java.util.UUID;

public record AuditRecordSearchRequest(
        String eventType,
        String aggregateType,
        UUID aggregateId,
        String sourceService,
        String severity,
        String outcome,
        Instant occurredFrom,
        Instant occurredTo,
        Integer page,
        Integer size
) {
}