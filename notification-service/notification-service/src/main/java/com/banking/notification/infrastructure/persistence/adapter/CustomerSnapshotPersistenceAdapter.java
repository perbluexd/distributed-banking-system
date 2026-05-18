package com.banking.notification.infrastructure.persistence.adapter;

import com.banking.notification.application.port.out.CustomerSnapshotRepositoryPort;
import com.banking.notification.domain.model.CustomerId;
import com.banking.notification.domain.model.CustomerSnapshot;
import com.banking.notification.infrastructure.persistence.mapper.CustomerSnapshotPersistenceMapper;
import com.banking.notification.infrastructure.persistence.repository.CustomerSnapshotJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomerSnapshotPersistenceAdapter implements CustomerSnapshotRepositoryPort {

    private final CustomerSnapshotJpaRepository customerSnapshotJpaRepository;

    public CustomerSnapshotPersistenceAdapter(
            CustomerSnapshotJpaRepository customerSnapshotJpaRepository
    ) {
        this.customerSnapshotJpaRepository = customerSnapshotJpaRepository;
    }

    @Override
    public CustomerSnapshot save(CustomerSnapshot customerSnapshot) {
        customerSnapshotJpaRepository.save(
                CustomerSnapshotPersistenceMapper.toEntity(customerSnapshot)
        );

        return customerSnapshot;
    }

    @Override
    public Optional<CustomerSnapshot> findByCustomerId(CustomerId customerId) {
        return customerSnapshotJpaRepository
                .findById(customerId.value())
                .map(CustomerSnapshotPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByCustomerId(CustomerId customerId) {
        return customerSnapshotJpaRepository.existsByCustomerId(
                customerId.value()
        );
    }
}