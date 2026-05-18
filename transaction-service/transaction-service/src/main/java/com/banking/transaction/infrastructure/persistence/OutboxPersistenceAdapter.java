package com.banking.transaction.infrastructure.persistence;

import com.banking.transaction.application.port.out.OutboxEventRepositoryPort;
import com.banking.transaction.domain.model.OutboxEvent;
import com.banking.transaction.domain.model.OutboxEventStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxPersistenceAdapter implements OutboxEventRepositoryPort {

    private final OutboxEventJpaRepository outboxEventJpaRepository;

    public OutboxPersistenceAdapter(OutboxEventJpaRepository outboxEventJpaRepository) {
        this.outboxEventJpaRepository = outboxEventJpaRepository;
    }

    @Override
    public OutboxEvent save(OutboxEvent event) {
        OutboxEventJpaEntity entity = OutboxEventPersistenceMapper.toEntity(event);
        outboxEventJpaRepository.save(entity);
        return event;
    }

    @Override
    public List<OutboxEvent> findPendingEvents(int limit) {
        return outboxEventJpaRepository
                .findByStatusOrderByCreatedAtAsc(
                        OutboxEventStatus.PENDING,
                        PageRequest.of(0, limit)
                )
                .stream()
                .map(entity -> OutboxEvent.restore(
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
                ))
                .toList();
    }
}