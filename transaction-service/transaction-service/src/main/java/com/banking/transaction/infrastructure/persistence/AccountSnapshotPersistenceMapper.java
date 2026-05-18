package com.banking.transaction.infrastructure.persistence;

import com.banking.transaction.domain.model.AccountId;
import com.banking.transaction.domain.model.AccountSnapshot;
import com.banking.transaction.domain.model.CustomerId;

import java.time.Instant;

public class AccountSnapshotPersistenceMapper {

    private AccountSnapshotPersistenceMapper() {
    }

    public static AccountSnapshotJpaEntity toEntity(AccountSnapshot snapshot) {
        return new AccountSnapshotJpaEntity(
                snapshot.getAccountId().value(),
                snapshot.getCustomerId().value(),
                snapshot.getCurrency(),
                snapshot.getBalance(),
                snapshot.isActive(),
                Instant.now()
        );
    }

    public static AccountSnapshot toDomain(AccountSnapshotJpaEntity entity) {
        return new AccountSnapshot(
                new AccountId(entity.getAccountId()),
                new CustomerId(entity.getCustomerId()),
                entity.getCurrency(),
                entity.getBalance(),
                entity.isActive()
        );
    }
}