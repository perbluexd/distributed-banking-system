package com.banca.customer.application.port.out;

import com.banca.customer.domain.model.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepositoryPort {

    Customer save(Customer customer);

    Optional<Customer> findById(UUID id);

    Optional<Customer> findByUserId(UUID userId);

    boolean existsById(UUID id);

    boolean existsByUserId(UUID userId);

    boolean existsByEmail(String email);
}