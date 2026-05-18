package com.banca.customer.infrastructure.persistence.adapter;

import com.banca.customer.application.port.out.OutboxEventRepositoryPort;
import com.banca.customer.domain.model.OutboxEvent;
import com.banca.customer.domain.model.OutboxEventStatus;
import com.banca.customer.infrastructure.persistence.mapper.OutboxEventPersistenceMapper;
import com.banca.customer.infrastructure.persistence.repository.OutboxEventJpaRepository;
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
                repository.save(OutboxEventPersistenceMapper.toEntity(event))
        );
    }

    @Override
    public List<OutboxEvent> findPendingEvents(int limit) {
        return repository.findByStatusOrderByCreatedAtAsc(
                        OutboxEventStatus.PENDING,
                        PageRequest.of(0, limit)
                )
                .stream()
                .map(OutboxEventPersistenceMapper::toDomain)
                .toList();
    }
}