package com.banking.account.infrastructure.persistence;

import com.banking.account.application.port.out.CustomerSnapshotRepositoryPort;
import com.banking.account.domain.model.CustomerId;
import com.banking.account.domain.model.CustomerSnapshot;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomerSnapshotPersistenceAdapter implements CustomerSnapshotRepositoryPort {

    private final CustomerSnapshotJpaRepository repository;

    public CustomerSnapshotPersistenceAdapter(CustomerSnapshotJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public CustomerSnapshot save(CustomerSnapshot snapshot) {
        return CustomerSnapshotPersistenceMapper.toDomain(
                repository.save(
                        CustomerSnapshotPersistenceMapper.toEntity(snapshot)
                )
        );
    }

    @Override
    public Optional<CustomerSnapshot> findByCustomerId(CustomerId customerId) {
        return repository.findById(customerId.value())
                .map(CustomerSnapshotPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByCustomerId(CustomerId customerId) {
        return repository.existsByCustomerId(customerId.value());
    }
}