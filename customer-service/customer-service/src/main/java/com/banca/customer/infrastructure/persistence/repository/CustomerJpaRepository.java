package com.banca.customer.infrastructure.persistence.repository;

import com.banca.customer.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {

    Optional<CustomerEntity> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    boolean existsByEmail(String email);
}