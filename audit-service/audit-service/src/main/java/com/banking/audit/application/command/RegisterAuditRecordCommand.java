package com.banking.audit.application.command;

import com.banking.audit.domain.model.AuditAction;
import com.banking.audit.domain.model.AuditActor;
import com.banking.audit.domain.model.AuditAggregateId;
import com.banking.audit.domain.model.AuditAggregateType;
import com.banking.audit.domain.model.AuditEventId;
import com.banking.audit.domain.model.AuditEventType;
import com.banking.audit.domain.model.AuditOutcome;
import com.banking.audit.domain.model.AuditSeverity;
import com.banking.audit.domain.model.AuditSourceService;

import java.time.Instant;
import java.util.Objects;

public record RegisterAuditRecordCommand(
        AuditEventId eventId,
        AuditEventType eventType,
        AuditAggregateType aggregateType,
        AuditAggregateId aggregateId,
        AuditSourceService sourceService,
        AuditSeverity severity,
        AuditActor actor,
        AuditAction action,
        AuditOutcome outcome,
        String payloadJson,
        Instant occurredAt
) {

    public RegisterAuditRecordCommand {
        Objects.requireNonNull(eventId, "Event id cannot be null");
        Objects.requireNonNull(eventType, "Event type cannot be null");
        Objects.requireNonNull(aggregateType, "Aggregate type cannot be null");
        Objects.requireNonNull(aggregateId, "Aggregate id cannot be null");
        Objects.requireNonNull(sourceService, "Source service cannot be null");
        Objects.requireNonNull(severity, "Severity cannot be null");
        Objects.requireNonNull(actor, "Actor cannot be null");
        Objects.requireNonNull(action, "Action cannot be null");
        Objects.requireNonNull(outcome, "Outcome cannot be null");
        Objects.requireNonNull(payloadJson, "Payload json cannot be null");
        Objects.requireNonNull(occurredAt, "Occurred at cannot be null");

        if (payloadJson.isBlank()) {
            throw new IllegalArgumentException("Payload json cannot be blank");
        }
    }
}