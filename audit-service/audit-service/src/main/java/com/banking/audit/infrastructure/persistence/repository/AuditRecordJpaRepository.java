package com.banking.audit.infrastructure.persistence.repository;

import com.banking.audit.infrastructure.persistence.entity.AuditRecordJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface AuditRecordJpaRepository extends
        JpaRepository<AuditRecordJpaEntity, UUID>,
        JpaSpecificationExecutor<AuditRecordJpaEntity> {

    boolean existsByEventId(UUID eventId);
}