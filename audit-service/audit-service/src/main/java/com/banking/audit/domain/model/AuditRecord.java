package com.banking.audit.domain.model;

import java.time.Instant;
import java.util.Objects;

public class AuditRecord {

    private final AuditRecordId id;
    private final AuditEventId eventId;
    private final AuditEventType eventType;
    private final AuditAggregateType aggregateType;
    private final AuditAggregateId aggregateId;
    private final AuditSourceService sourceService;
    private final AuditSeverity severity;
    private final AuditActor actor;
    private final AuditAction action;
    private final AuditOutcome outcome;
    private final String payloadJson;
    private final Instant occurredAt;
    private final Instant recordedAt;

    private AuditRecord(
            AuditRecordId id,
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
            Instant occurredAt,
            Instant recordedAt
    ) {
        this.id = Objects.requireNonNull(id, "Audit record id cannot be null");
        this.eventId = Objects.requireNonNull(eventId, "Audit event id cannot be null");
        this.eventType = Objects.requireNonNull(eventType, "Audit event type cannot be null");
        this.aggregateType = Objects.requireNonNull(aggregateType, "Audit aggregate type cannot be null");
        this.aggregateId = Objects.requireNonNull(aggregateId, "Audit aggregate id cannot be null");
        this.sourceService = Objects.requireNonNull(sourceService, "Audit source service cannot be null");
        this.severity = Objects.requireNonNull(severity, "Audit severity cannot be null");
        this.actor = Objects.requireNonNull(actor, "Audit actor cannot be null");
        this.action = Objects.requireNonNull(action, "Audit action cannot be null");
        this.outcome = Objects.requireNonNull(outcome, "Audit outcome cannot be null");
        this.payloadJson = validatePayload(payloadJson);
        this.occurredAt = Objects.requireNonNull(occurredAt, "Occurred at cannot be null");
        this.recordedAt = Objects.requireNonNull(recordedAt, "Recorded at cannot be null");
    }

    public static AuditRecord register(
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
        return new AuditRecord(
                AuditRecordId.newId(),
                eventId,
                eventType,
                aggregateType,
                aggregateId,
                sourceService,
                severity,
                actor,
                action,
                outcome,
                payloadJson,
                occurredAt,
                Instant.now()
        );
    }

    public static AuditRecord restore(
            AuditRecordId id,
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
            Instant occurredAt,
            Instant recordedAt
    ) {
        return new AuditRecord(
                id,
                eventId,
                eventType,
                aggregateType,
                aggregateId,
                sourceService,
                severity,
                actor,
                action,
                outcome,
                payloadJson,
                occurredAt,
                recordedAt
        );
    }

    private static String validatePayload(String payloadJson) {
        Objects.requireNonNull(payloadJson, "Payload json cannot be null");

        if (payloadJson.isBlank()) {
            throw new IllegalArgumentException("Payload json cannot be blank");
        }

        return payloadJson;
    }

    public AuditRecordId getId() {
        return id;
    }

    public AuditEventId getEventId() {
        return eventId;
    }

    public AuditEventType getEventType() {
        return eventType;
    }

    public AuditAggregateType getAggregateType() {
        return aggregateType;
    }

    public AuditAggregateId getAggregateId() {
        return aggregateId;
    }

    public AuditSourceService getSourceService() {
        return sourceService;
    }

    public AuditSeverity getSeverity() {
        return severity;
    }

    public AuditActor getActor() {
        return actor;
    }

    public AuditAction getAction() {
        return action;
    }

    public AuditOutcome getOutcome() {
        return outcome;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }
}