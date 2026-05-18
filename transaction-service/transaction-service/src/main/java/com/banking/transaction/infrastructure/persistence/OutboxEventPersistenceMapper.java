package com.banking.transaction.infrastructure.persistence;

import com.banking.transaction.domain.model.OutboxEvent;

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
}