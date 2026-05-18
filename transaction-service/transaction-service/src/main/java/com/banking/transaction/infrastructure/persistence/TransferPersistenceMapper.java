package com.banking.transaction.infrastructure.persistence;

import com.banking.transaction.domain.model.*;

public class TransferPersistenceMapper {

    private TransferPersistenceMapper() {
    }

    public static TransferJpaEntity toEntity(Transfer transfer) {
        return new TransferJpaEntity(
                transfer.getId().value(),
                transfer.getSourceAccountId().value(),
                transfer.getTargetAccountId().value(),
                transfer.getAmount().amount(),
                transfer.getAmount().currency(),
                transfer.getType(),
                transfer.getIdempotencyKey().value(),
                transfer.getStatus(),
                transfer.getCreatedAt(),
                transfer.getUpdatedAt()
        );
    }

    public static Transfer toDomain(TransferJpaEntity entity) {
        return Transfer.restore(
                new TransferId(entity.getId()),
                new AccountId(entity.getSourceAccountId()),
                new AccountId(entity.getTargetAccountId()),
                new Money(entity.getAmount(), entity.getCurrency()),
                entity.getType(),
                new IdempotencyKey(entity.getIdempotencyKey()),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}