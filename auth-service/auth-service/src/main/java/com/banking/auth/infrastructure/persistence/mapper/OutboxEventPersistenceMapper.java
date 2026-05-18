package com.banking.auth.infrastructure.persistence.mapper;

import com.banking.auth.domain.model.OutboxEvent;
import com.banking.auth.infrastructure.persistence.entity.OutboxEventJpaEntity;

public class OutboxEventPersistenceMapper {

    private OutboxEventPersistenceMapper() {
    }

    public static OutboxEventJpaEntity toEntity(OutboxEvent event) {
        return new OutboxEventJpaEntity(
                event.getId(),
                event.getAggregateType(),
                event.getAggregateId(),
                event.getEventType(),
                event.getTopic(),
                event.getEventKey(),
                event.getPayload(),
                event.getStatus(),
                event.getCreatedAt(),
                event.getPublishedAt()
        );
    }

    public static OutboxEvent toDomain(OutboxEventJpaEntity entity) {
        return OutboxEvent.restore(
                entity.getId(),
                entity.getAggregateType(),
                entity.getAggregateId(),
                entity.getEventType(),
                entity.getTopic(),
                entity.getEventKey(),
                entity.getPayload(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getPublishedAt()
        );
    }
}