package com.banking.audit.api.dto;

import java.time.Instant;
import java.util.UUID;

public record AuditRecordResponse(
        UUID id,
        UUID eventId,
        String eventType,
        String aggregateType,
        UUID aggregateId,
        String sourceService,
        String severity,
        UUID actorId,
        String actorType,
        String action,
        String outcome,
        String payloadJson,
        Instant occurredAt,
        Instant recordedAt
) {
}