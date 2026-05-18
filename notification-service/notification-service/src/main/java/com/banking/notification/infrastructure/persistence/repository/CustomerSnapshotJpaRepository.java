package com.banking.notification.infrastructure.persistence.repository;

import com.banking.notification.infrastructure.persistence.entity.CustomerSnapshotJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerSnapshotJpaRepository extends JpaRepository<CustomerSnapshotJpaEntity, UUID> {

    boolean existsByCustomerId(UUID customerId);
}