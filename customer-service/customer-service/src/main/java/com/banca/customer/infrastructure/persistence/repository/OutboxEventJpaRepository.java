package com.banca.customer.infrastructure.persistence.repository;

import com.banca.customer.domain.model.OutboxEventStatus;
import com.banca.customer.infrastructure.persistence.entity.OutboxEventJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {

    List<OutboxEventJpaEntity> findByStatusOrderByCreatedAtAsc(
            OutboxEventStatus status,
            Pageable pageable
    );
}