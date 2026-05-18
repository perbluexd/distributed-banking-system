package com.banking.transaction.infrastructure.persistence;

import com.banking.transaction.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TransferPersistenceAdapter.class)
class TransferPersistenceAdapterTest {

    @Autowired
    private TransferPersistenceAdapter adapter;

    @Test
    void shouldSaveAndFindTransferById() {
        Transfer transfer = Transfer.create(
                new AccountId(UUID.randomUUID()),
                new AccountId(UUID.randomUUID()),
                new Money(new BigDecimal("100.00"), Currency.PEN),
                TransferType.INTERNAL,
                new IdempotencyKey("key-123")
        );

        Transfer saved = adapter.save(transfer);

        Optional<Transfer> found = adapter.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(TransferStatus.PENDING, found.get().getStatus());
    }

    @Test
    void shouldFindTransfersByAccountId() {
        AccountId sourceAccountId = new AccountId(UUID.randomUUID());
        AccountId targetAccountId = new AccountId(UUID.randomUUID());

        Transfer transfer = Transfer.create(
                sourceAccountId,
                targetAccountId,
                new Money(new BigDecimal("100.00"), Currency.PEN),
                TransferType.INTERNAL,
                new IdempotencyKey("key-abc")
        );

        adapter.save(transfer);

        List<Transfer> transfers = adapter.findByAccountId(sourceAccountId);

        assertEquals(1, transfers.size());
    }
}