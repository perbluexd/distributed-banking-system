package com.banking.account.infrastructure.persistence;

import com.banking.account.application.port.out.OutboxEventRepositoryPort;
import com.banking.account.domain.model.OutboxEvent;
import com.banking.account.domain.model.OutboxEventStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxEventPersistenceAdapter implements OutboxEventRepositoryPort {

    private final OutboxEventJpaRepository outboxEventJpaRepository;

    public OutboxEventPersistenceAdapter(OutboxEventJpaRepository outboxEventJpaRepository) {
        this.outboxEventJpaRepository = outboxEventJpaRepository;
    }

    @Override
    public OutboxEvent save(OutboxEvent event) {
        OutboxEventJpaEntity entity = OutboxEventPersistenceMapper.toEntity(event);
        OutboxEventJpaEntity saved = outboxEventJpaRepository.save(entity);
        return OutboxEventPersistenceMapper.toDomain(saved);
    }

    @Override
    public List<OutboxEvent> findPendingEvents(int limit) {
        return outboxEventJpaRepository
                .findByStatusOrderByCreatedAtAsc(
                        OutboxEventStatus.PENDING,
                        PageRequest.of(0, limit)
                )
                .stream()
                .map(OutboxEventPersistenceMapper::toDomain)
                .toList();
    }
}