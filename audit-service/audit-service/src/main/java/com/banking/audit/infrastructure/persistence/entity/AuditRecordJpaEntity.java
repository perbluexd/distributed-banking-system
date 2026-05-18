package com.banking.audit.infrastructure.persistence.entity;

import com.banking.audit.domain.model.AuditAction;
import com.banking.audit.domain.model.AuditAggregateType;
import com.banking.audit.domain.model.AuditEventType;
import com.banking.audit.domain.model.AuditOutcome;
import com.banking.audit.domain.model.AuditSeverity;
import com.banking.audit.domain.model.AuditSourceService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_records")
public class AuditRecordJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "event_id", nullable = false, unique = true, updatable = false)
    private UUID eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, updatable = false, length = 80)
    private AuditEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "aggregate_type", nullable = false, updatable = false, length = 80)
    private AuditAggregateType aggregateType;

    @Column(name = "aggregate_id", nullable = false, updatable = false)
    private UUID aggregateId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_service", nullable = false, updatable = false, length = 80)
    private AuditSourceService sourceService;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, updatable = false, length = 40)
    private AuditSeverity severity;

    @Column(name = "actor_id", updatable = false)
    private UUID actorId;

    @Column(name = "actor_type", nullable = false, updatable = false, length = 80)
    private String actorType;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, updatable = false, length = 80)
    private AuditAction action;

    @Enumerated(EnumType.STRING)
    @Column(name = "outcome", nullable = false, updatable = false, length = 40)
    private AuditOutcome outcome;

    @Column(name = "payload_json", nullable = false, updatable = false, columnDefinition = "TEXT")
    private String payloadJson;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    @Column(name = "recorded_at", nullable = false, updatable = false)
    private Instant recordedAt;

    protected AuditRecordJpaEntity() {
    }

    public AuditRecordJpaEntity(
            UUID id,
            UUID eventId,
            AuditEventType eventType,
            AuditAggregateType aggregateType,
            UUID aggregateId,
            AuditSourceService sourceService,
            AuditSeverity severity,
            UUID actorId,
            String actorType,
            AuditAction action,
            AuditOutcome outcome,
            String payloadJson,
            Instant occurredAt,
            Instant recordedAt
    ) {
        this.id = id;
        this.eventId = eventId;
        this.eventType = eventType;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.sourceService = sourceService;
        this.severity = severity;
        this.actorId = actorId;
        this.actorType = actorType;
        this.action = action;
        this.outcome = outcome;
        this.payloadJson = payloadJson;
        this.occurredAt = occurredAt;
        this.recordedAt = recordedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public AuditEventType getEventType() {
        return eventType;
    }

    public AuditAggregateType getAggregateType() {
        return aggregateType;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public AuditSourceService getSourceService() {
        return sourceService;
    }

    public AuditSeverity getSeverity() {
        return severity;
    }

    public UUID getActorId() {
        return actorId;
    }

    public String getActorType() {
        return actorType;
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