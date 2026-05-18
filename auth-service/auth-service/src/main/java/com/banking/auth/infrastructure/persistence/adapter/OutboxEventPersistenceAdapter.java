package com.banking.auth.infrastructure.persistence.adapter;

import com.banking.auth.application.port.out.OutboxEventRepositoryPort;
import com.banking.auth.domain.model.OutboxEvent;
import com.banking.auth.domain.model.OutboxEventStatus;
import com.banking.auth.infrastructure.persistence.mapper.OutboxEventPersistenceMapper;
import com.banking.auth.infrastructure.persistence.repository.OutboxEventJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxEventPersistenceAdapter implements OutboxEventRepositoryPort {

    private final OutboxEventJpaRepository repository;

    public OutboxEventPersistenceAdapter(OutboxEventJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public OutboxEvent save(OutboxEvent event) {
        return OutboxEventPersistenceMapper.toDomain(
                repository.save(
                        OutboxEventPersistenceMapper.toEntity(event)
                )
        );
    }

    @Override
    public List<OutboxEvent> findPendingEvents(int limit) {
        return repository
                .findByStatusOrderByCreatedAtAsc(
                        OutboxEventStatus.PENDING,
                        PageRequest.of(0, limit)
                )
                .stream()
                .map(OutboxEventPersistenceMapper::toDomain)
                .toList();
    }
}