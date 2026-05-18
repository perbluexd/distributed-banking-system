package com.banking.notification.infrastructure.persistence.mapper;

import com.banking.notification.domain.model.CustomerId;
import com.banking.notification.domain.model.CustomerSnapshot;
import com.banking.notification.infrastructure.persistence.entity.CustomerSnapshotJpaEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerSnapshotPersistenceMapperTest {

    @Test
    void shouldMapDomainToEntity() {
        CustomerSnapshot snapshot = CustomerSnapshot.create(
                CustomerId.of(UUID.randomUUID()),
                UUID.randomUUID(),
                "customer@example.com",
                "ACTIVE"
        );

        CustomerSnapshotJpaEntity entity =
                CustomerSnapshotPersistenceMapper.toEntity(snapshot);

        assertEquals(snapshot.getCustomerId().value(), entity.getCustomerId());
        assertEquals(snapshot.getUserId(), entity.getUserId());
        assertEquals(snapshot.getEmail(), entity.getEmail());
        assertEquals(snapshot.getStatus(), entity.getStatus());
        assertEquals(snapshot.getCreatedAt(), entity.getCreatedAt());
        assertEquals(snapshot.getUpdatedAt(), entity.getUpdatedAt());
    }

    @Test
    void shouldMapEntityToDomain() {
        CustomerSnapshot snapshot = CustomerSnapshot.create(
                CustomerId.of(UUID.randomUUID()),
                UUID.randomUUID(),
                "customer@example.com",
                "ACTIVE"
        );

        CustomerSnapshotJpaEntity entity =
                CustomerSnapshotPersistenceMapper.toEntity(snapshot);

        CustomerSnapshot result =
                CustomerSnapshotPersistenceMapper.toDomain(entity);

        assertEquals(snapshot.getCustomerId(), result.getCustomerId());
        assertEquals(snapshot.getUserId(), result.getUserId());
        assertEquals(snapshot.getEmail(), result.getEmail());
        assertEquals(snapshot.getStatus(), result.getStatus());
    }
}