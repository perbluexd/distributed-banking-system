package com.banking.account.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerSnapshotJpaRepository extends JpaRepository<CustomerSnapshotJpaEntity, UUID> {

    boolean existsByCustomerId(UUID customerId);
}