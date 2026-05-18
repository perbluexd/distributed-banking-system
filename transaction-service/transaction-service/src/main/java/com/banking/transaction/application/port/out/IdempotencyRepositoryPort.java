package com.banking.transaction.application.port.out;

import com.banking.transaction.domain.model.IdempotencyKey;

import java.util.Optional;
import java.util.UUID;

public interface IdempotencyRepositoryPort {

    void save(IdempotencyKey key, UUID transferId);

    Optional<UUID> findTransferIdByKey(IdempotencyKey key);

    boolean exists(IdempotencyKey key);
}