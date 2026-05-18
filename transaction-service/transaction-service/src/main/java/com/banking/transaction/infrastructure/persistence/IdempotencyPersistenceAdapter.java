package com.banking.transaction.infrastructure.persistence;

import com.banking.transaction.application.port.out.IdempotencyRepositoryPort;
import com.banking.transaction.domain.model.IdempotencyKey;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class IdempotencyPersistenceAdapter implements IdempotencyRepositoryPort {

    private final IdempotencyKeyJpaRepository idempotencyKeyJpaRepository;

    public IdempotencyPersistenceAdapter(IdempotencyKeyJpaRepository idempotencyKeyJpaRepository) {
        this.idempotencyKeyJpaRepository = idempotencyKeyJpaRepository;
    }

    @Override
    public void save(IdempotencyKey key, UUID transferId) {
        IdempotencyKeyJpaEntity entity = new IdempotencyKeyJpaEntity(
                key.value(),
                transferId,
                Instant.now()
        );

        idempotencyKeyJpaRepository.save(entity);
    }

    @Override
    public Optional<UUID> findTransferIdByKey(IdempotencyKey key) {
        return idempotencyKeyJpaRepository.findById(key.value())
                .map(IdempotencyKeyJpaEntity::getTransferId);
    }

    @Override
    public boolean exists(IdempotencyKey key) {
        return idempotencyKeyJpaRepository.existsById(key.value());
    }
}