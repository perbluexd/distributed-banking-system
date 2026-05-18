package com.banking.transaction.integration;

import com.banking.transaction.domain.model.*;
import com.banking.transaction.infrastructure.persistence.TransferPersistenceAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class PostgreSqlTestcontainersTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TransferPersistenceAdapter transferPersistenceAdapter;

    @Test
    void shouldSaveTransferUsingRealPostgresContainer() {
        Transfer transfer = Transfer.create(
                new AccountId(UUID.randomUUID()),
                new AccountId(UUID.randomUUID()),
                new Money(new BigDecimal("150.00"), Currency.PEN),
                TransferType.INTERNAL,
                new IdempotencyKey("integration-key-123")
        );

        Transfer saved = transferPersistenceAdapter.save(transfer);

        Optional<Transfer> found = transferPersistenceAdapter.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }
}