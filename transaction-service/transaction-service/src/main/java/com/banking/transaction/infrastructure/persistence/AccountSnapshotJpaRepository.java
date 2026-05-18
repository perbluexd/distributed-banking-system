package com.banking.transaction.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountSnapshotJpaRepository extends JpaRepository<AccountSnapshotJpaEntity, UUID> {
}