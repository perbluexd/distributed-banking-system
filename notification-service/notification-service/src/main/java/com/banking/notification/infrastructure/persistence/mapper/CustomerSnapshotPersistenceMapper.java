package com.banking.notification.infrastructure.persistence.mapper;

import com.banking.notification.domain.model.CustomerId;
import com.banking.notification.domain.model.CustomerSnapshot;
import com.banking.notification.infrastructure.persistence.entity.CustomerSnapshotJpaEntity;

public class CustomerSnapshotPersistenceMapper {

    private CustomerSnapshotPersistenceMapper() {
    }

    public static CustomerSnapshotJpaEntity toEntity(CustomerSnapshot snapshot) {
        return new CustomerSnapshotJpaEntity(
                snapshot.getCustomerId().value(),
                snapshot.getUserId(),
                snapshot.getEmail(),
                snapshot.getStatus(),
                snapshot.getCreatedAt(),
                snapshot.getUpdatedAt()
        );
    }

    public static CustomerSnapshot toDomain(CustomerSnapshotJpaEntity entity) {
        return CustomerSnapshot.restore(
                CustomerId.of(entity.getCustomerId()),
                entity.getUserId(),
                entity.getEmail(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}