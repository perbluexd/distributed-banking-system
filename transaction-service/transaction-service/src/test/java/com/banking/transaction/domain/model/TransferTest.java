package com.banking.transaction.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransferTest {

    @Test
    void shouldCreatePendingTransfer() {
        AccountId sourceAccountId = new AccountId(UUID.randomUUID());
        AccountId targetAccountId = new AccountId(UUID.randomUUID());
        Money amount = new Money(new BigDecimal("150.00"), Currency.PEN);
        IdempotencyKey idempotencyKey = new IdempotencyKey("key-123");

        Transfer transfer = Transfer.create(
                sourceAccountId,
                targetAccountId,
                amount,
                TransferType.INTERNAL,
                idempotencyKey
        );

        assertNotNull(transfer.getId());
        assertEquals(sourceAccountId, transfer.getSourceAccountId());
        assertEquals(targetAccountId, transfer.getTargetAccountId());
        assertEquals(amount, transfer.getAmount());
        assertEquals(TransferType.INTERNAL, transfer.getType());
        assertEquals(idempotencyKey, transfer.getIdempotencyKey());
        assertEquals(TransferStatus.PENDING, transfer.getStatus());
        assertNotNull(transfer.getCreatedAt());
        assertNotNull(transfer.getUpdatedAt());
    }

    @Test
    void shouldRejectSameSourceAndTargetAccount() {
        AccountId accountId = new AccountId(UUID.randomUUID());
        Money amount = new Money(new BigDecimal("150.00"), Currency.PEN);

        assertThrows(IllegalArgumentException.class, () ->
                Transfer.create(
                        accountId,
                        accountId,
                        amount,
                        TransferType.INTERNAL,
                        new IdempotencyKey("key-123")
                )
        );
    }

    @Test
    void shouldMarkPendingTransferAsCompleted() {
        Transfer transfer = createPendingTransfer();

        transfer.markCompleted();

        assertEquals(TransferStatus.COMPLETED, transfer.getStatus());
    }

    @Test
    void shouldMarkPendingTransferAsFailed() {
        Transfer transfer = createPendingTransfer();

        transfer.markFailed();

        assertEquals(TransferStatus.FAILED, transfer.getStatus());
    }

    @Test
    void shouldNotCompleteAlreadyCompletedTransfer() {
        Transfer transfer = createPendingTransfer();
        transfer.markCompleted();

        assertThrows(IllegalStateException.class, transfer::markCompleted);
    }

    @Test
    void shouldNotFailAlreadyCompletedTransfer() {
        Transfer transfer = createPendingTransfer();
        transfer.markCompleted();

        assertThrows(IllegalStateException.class, transfer::markFailed);
    }

    @Test
    void shouldRestoreTransfer() {
        TransferId transferId = new TransferId(UUID.randomUUID());
        AccountId sourceAccountId = new AccountId(UUID.randomUUID());
        AccountId targetAccountId = new AccountId(UUID.randomUUID());
        Money amount = new Money(new BigDecimal("80.00"), Currency.PEN);
        Instant now = Instant.now();

        Transfer transfer = Transfer.restore(
                transferId,
                sourceAccountId,
                targetAccountId,
                amount,
                TransferType.INTERNAL,
                new IdempotencyKey("restore-key"),
                TransferStatus.PENDING,
                now,
                now
        );

        assertEquals(transferId, transfer.getId());
        assertEquals(TransferStatus.PENDING, transfer.getStatus());
    }

    private Transfer createPendingTransfer() {
        return Transfer.create(
                new AccountId(UUID.randomUUID()),
                new AccountId(UUID.randomUUID()),
                new Money(new BigDecimal("100.00"), Currency.PEN),
                TransferType.INTERNAL,
                new IdempotencyKey(UUID.randomUUID().toString())
        );
    }
}