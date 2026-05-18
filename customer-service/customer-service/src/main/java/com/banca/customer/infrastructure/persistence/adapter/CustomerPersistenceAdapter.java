package com.banca.customer.infrastructure.persistence.adapter;

import com.banca.customer.application.port.out.CustomerRepositoryPort;
import com.banca.customer.domain.model.Customer;
import com.banca.customer.infrastructure.persistence.mapper.CustomerPersistenceMapper;
import com.banca.customer.infrastructure.persistence.repository.CustomerJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerPersistenceAdapter implements CustomerRepositoryPort {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerPersistenceMapper customerPersistenceMapper;

    public CustomerPersistenceAdapter(CustomerJpaRepository customerJpaRepository,
                                      CustomerPersistenceMapper customerPersistenceMapper) {
        this.customerJpaRepository = customerJpaRepository;
        this.customerPersistenceMapper = customerPersistenceMapper;
    }

    @Override
    public Customer save(Customer customer) {
        return customerPersistenceMapper.toDomain(
                customerJpaRepository.save(
                        customerPersistenceMapper.toEntity(customer)
                )
        );
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return customerJpaRepository.findById(id)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Customer> findByUserId(UUID userId) {
        return customerJpaRepository.findByUserId(userId)
                .map(customerPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return customerJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return customerJpaRepository.existsByUserId(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return customerJpaRepository.existsByEmail(email);
    }
}