package com.banking.notification.infrastructure.persistence.adapter;

import com.banking.notification.domain.model.CustomerId;
import com.banking.notification.domain.model.CustomerSnapshot;
import com.banking.notification.infrastructure.persistence.repository.CustomerSnapshotJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerSnapshotPersistenceAdapterTest {

    private CustomerSnapshotPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        CustomerSnapshotJpaRepository repository =
                org.mockito.Mockito.mock(CustomerSnapshotJpaRepository.class);

        adapter = new CustomerSnapshotPersistenceAdapter(repository);
    }

    @Test
    void shouldReturnSameCustomerSnapshotWhenSaving() {
        CustomerSnapshot snapshot = CustomerSnapshot.create(
                CustomerId.of(UUID.randomUUID()),
                UUID.randomUUID(),
                "customer@example.com",
                "ACTIVE"
        );

        CustomerSnapshot saved = adapter.save(snapshot);

        assertSame(snapshot, saved);
    }
}