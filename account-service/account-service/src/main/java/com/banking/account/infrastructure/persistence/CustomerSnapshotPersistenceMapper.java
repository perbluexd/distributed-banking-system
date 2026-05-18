package com.banking.account.infrastructure.persistence;

import com.banking.account.domain.model.CustomerSnapshot;

public class CustomerSnapshotPersistenceMapper {

    private CustomerSnapshotPersistenceMapper() {
    }

    public static CustomerSnapshotJpaEntity toEntity(CustomerSnapshot snapshot) {
        return new CustomerSnapshotJpaEntity(
                snapshot.getCustomerId(),
                snapshot.getUserId(),
                snapshot.getEmail(),
                snapshot.getStatus(),
                snapshot.getCreatedAt(),
                snapshot.getLastEventAt()
        );
    }

    public static CustomerSnapshot toDomain(CustomerSnapshotJpaEntity entity) {
        return CustomerSnapshot.create(
                entity.getCustomerId(),
                entity.getUserId(),
                entity.getEmail(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getLastEventAt()
        );
    }
}