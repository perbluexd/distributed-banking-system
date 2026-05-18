package com.banking.account.application.port.out;

import com.banking.account.domain.model.CustomerId;
import com.banking.account.domain.model.CustomerSnapshot;

import java.util.Optional;

public interface CustomerSnapshotRepositoryPort {

    CustomerSnapshot save(CustomerSnapshot snapshot);

    Optional<CustomerSnapshot> findByCustomerId(CustomerId customerId);

    boolean existsByCustomerId(CustomerId customerId);
}