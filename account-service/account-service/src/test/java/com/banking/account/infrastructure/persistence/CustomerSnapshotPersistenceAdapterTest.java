package com.banking.account.infrastructure.persistence;

import com.banking.account.domain.model.CustomerId;
import com.banking.account.domain.model.CustomerSnapshot;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerSnapshotPersistenceAdapterTest {

    private final CustomerSnapshotJpaRepository repository =
            mock(CustomerSnapshotJpaRepository.class);

    private final CustomerSnapshotPersistenceAdapter adapter =
            new CustomerSnapshotPersistenceAdapter(repository);

    @Test
    void shouldSaveCustomerSnapshot() {
        CustomerSnapshot snapshot = createSnapshot();

        when(repository.save(any(CustomerSnapshotJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CustomerSnapshot result = adapter.save(snapshot);

        assertNotNull(result);
        assertEquals(snapshot.getCustomerId(), result.getCustomerId());
        assertEquals(snapshot.getUserId(), result.getUserId());
        assertEquals(snapshot.getEmail(), result.getEmail());
        assertEquals(snapshot.getStatus(), result.getStatus());
        assertEquals(snapshot.getCreatedAt(), result.getCreatedAt());
        assertEquals(snapshot.getLastEventAt(), result.getLastEventAt());

        ArgumentCaptor<CustomerSnapshotJpaEntity> captor =
                ArgumentCaptor.forClass(CustomerSnapshotJpaEntity.class);

        verify(repository).save(captor.capture());

        CustomerSnapshotJpaEntity savedEntity = captor.getValue();

        assertEquals(snapshot.getCustomerId(), savedEntity.getCustomerId());
        assertEquals(snapshot.getUserId(), savedEntity.getUserId());
        assertEquals(snapshot.getEmail(), savedEntity.getEmail());
        assertEquals(snapshot.getStatus(), savedEntity.getStatus());
        assertEquals(snapshot.getCreatedAt(), savedEntity.getCreatedAt());
        assertEquals(snapshot.getLastEventAt(), savedEntity.getLastEventAt());
    }

    @Test
    void shouldFindCustomerSnapshotByCustomerId() {
        CustomerSnapshot snapshot = createSnapshot();

        CustomerSnapshotJpaEntity entity = new CustomerSnapshotJpaEntity(
                snapshot.getCustomerId(),
                snapshot.getUserId(),
                snapshot.getEmail(),
                snapshot.getStatus(),
                snapshot.getCreatedAt(),
                snapshot.getLastEventAt()
        );

        CustomerId customerId = CustomerId.of(snapshot.getCustomerId());

        when(repository.findById(snapshot.getCustomerId()))
                .thenReturn(Optional.of(entity));

        Optional<CustomerSnapshot> result = adapter.findByCustomerId(customerId);

        assertTrue(result.isPresent());
        assertEquals(snapshot.getCustomerId(), result.get().getCustomerId());
        assertEquals(snapshot.getUserId(), result.get().getUserId());
        assertEquals(snapshot.getEmail(), result.get().getEmail());
        assertEquals(snapshot.getStatus(), result.get().getStatus());
        assertEquals(snapshot.getCreatedAt(), result.get().getCreatedAt());
        assertEquals(snapshot.getLastEventAt(), result.get().getLastEventAt());

        verify(repository).findById(snapshot.getCustomerId());
    }

    @Test
    void shouldReturnEmptyWhenCustomerSnapshotIsNotFound() {
        UUID customerUuid = UUID.randomUUID();
        CustomerId customerId = CustomerId.of(customerUuid);

        when(repository.findById(customerUuid))
                .thenReturn(Optional.empty());

        Optional<CustomerSnapshot> result = adapter.findByCustomerId(customerId);

        assertTrue(result.isEmpty());

        verify(repository).findById(customerUuid);
    }

    @Test
    void shouldReturnTrueWhenCustomerSnapshotExists() {
        UUID customerUuid = UUID.randomUUID();
        CustomerId customerId = CustomerId.of(customerUuid);

        when(repository.existsByCustomerId(customerUuid))
                .thenReturn(true);

        boolean result = adapter.existsByCustomerId(customerId);

        assertTrue(result);

        verify(repository).existsByCustomerId(customerUuid);
    }

    @Test
    void shouldReturnFalseWhenCustomerSnapshotDoesNotExist() {
        UUID customerUuid = UUID.randomUUID();
        CustomerId customerId = CustomerId.of(customerUuid);

        when(repository.existsByCustomerId(customerUuid))
                .thenReturn(false);

        boolean result = adapter.existsByCustomerId(customerId);

        assertFalse(result);

        verify(repository).existsByCustomerId(customerUuid);
    }

    private CustomerSnapshot createSnapshot() {
        return CustomerSnapshot.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "customer@test.com",
                "ACTIVE",
                Instant.parse("2026-05-16T10:00:00Z"),
                Instant.parse("2026-05-16T10:05:00Z")
        );
    }
}