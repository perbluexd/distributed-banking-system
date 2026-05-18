package com.banking.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

public class OutboxEvent {

    private final UUID id;
    private final String aggregateType;
    private final UUID aggregateId;
    private final String eventType;
    private final String topic;
    private final String eventKey;
    private final String payload;
    private OutboxEventStatus status;
    private final Instant createdAt;
    private Instant publishedAt;

    private OutboxEvent(
            UUID id,
            String aggregateType,
            UUID aggregateId,
            String eventType,
            String topic,
            String eventKey,
            String payload,
            OutboxEventStatus status,
            Instant createdAt,
            Instant publishedAt
    ) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.topic = topic;
        this.eventKey = eventKey;
        this.payload = payload;
        this.status = status;
        this.createdAt = createdAt;
        this.publishedAt = publishedAt;
        validate();
    }

    public static OutboxEvent create(
            String aggregateType,
            UUID aggregateId,
            String eventType,
            String topic,
            String eventKey,
            String payload
    ) {
        return new OutboxEvent(
                UUID.randomUUID(),
                aggregateType,
                aggregateId,
                eventType,
                topic,
                eventKey,
                payload,
                OutboxEventStatus.PENDING,
                Instant.now(),
                null
        );
    }

    public static OutboxEvent restore(
            UUID id,
            String aggregateType,
            UUID aggregateId,
            String eventType,
            String topic,
            String eventKey,
            String payload,
            OutboxEventStatus status,
            Instant createdAt,
            Instant publishedAt
    ) {
        return new OutboxEvent(
                id,
                aggregateType,
                aggregateId,
                eventType,
                topic,
                eventKey,
                payload,
                status,
                createdAt,
                publishedAt
        );
    }

    public void markAsPublished() {
        this.status = OutboxEventStatus.PUBLISHED;
        this.publishedAt = Instant.now();
    }

    public void markAsFailed() {
        this.status = OutboxEventStatus.FAILED;
    }

    private void validate() {
        if (id == null) throw new IllegalArgumentException("Outbox event id cannot be null");
        if (aggregateType == null || aggregateType.isBlank()) throw new IllegalArgumentException("Aggregate type cannot be blank");
        if (aggregateId == null) throw new IllegalArgumentException("Aggregate id cannot be null");
        if (eventType == null || eventType.isBlank()) throw new IllegalArgumentException("Event type cannot be blank");
        if (topic == null || topic.isBlank()) throw new IllegalArgumentException("Topic cannot be blank");
        if (eventKey == null || eventKey.isBlank()) throw new IllegalArgumentException("Event key cannot be blank");
        if (payload == null || payload.isBlank()) throw new IllegalArgumentException("Payload cannot be blank");
        if (status == null) throw new IllegalArgumentException("Status cannot be null");
        if (createdAt == null) throw new IllegalArgumentException("CreatedAt cannot be null");
    }

    public UUID getId() { return id; }
    public String getAggregateType() { return aggregateType; }
    public UUID getAggregateId() { return aggregateId; }
    public String getEventType() { return eventType; }
    public String getTopic() { return topic; }
    public String getEventKey() { return eventKey; }
    public String getPayload() { return payload; }
    public OutboxEventStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getPublishedAt() { return publishedAt; }
}