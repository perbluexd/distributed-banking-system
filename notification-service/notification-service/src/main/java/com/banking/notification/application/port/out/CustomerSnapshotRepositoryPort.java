package com.banking.notification.application.port.out;

import com.banking.notification.domain.model.CustomerId;
import com.banking.notification.domain.model.CustomerSnapshot;

import java.util.Optional;

public interface CustomerSnapshotRepositoryPort {

    CustomerSnapshot save(CustomerSnapshot customerSnapshot);

    Optional<CustomerSnapshot> findByCustomerId(CustomerId customerId);

    boolean existsByCustomerId(CustomerId customerId);
}