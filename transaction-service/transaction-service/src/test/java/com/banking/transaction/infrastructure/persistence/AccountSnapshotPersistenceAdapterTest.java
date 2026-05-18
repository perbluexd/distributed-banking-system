package com.banking.transaction.infrastructure.persistence;

import com.banking.transaction.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(AccountSnapshotPersistenceAdapter.class)
class AccountSnapshotPersistenceAdapterTest {

    @Autowired
    private AccountSnapshotPersistenceAdapter adapter;

    @Test
    void shouldSaveAndFindAccountSnapshot() {
        AccountId accountId = new AccountId(UUID.randomUUID());

        AccountSnapshot snapshot = new AccountSnapshot(
                accountId,
                new CustomerId(UUID.randomUUID()),
                Currency.PEN,
                new BigDecimal("500.00"),
                true
        );

        adapter.save(snapshot);

        Optional<AccountSnapshot> found = adapter.findByAccountId(accountId);

        assertTrue(found.isPresent());
        assertEquals(accountId, found.get().getAccountId());
        assertEquals(Currency.PEN, found.get().getCurrency());
        assertTrue(found.get().isActive());
    }
}