package com.banking.transaction.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountSnapshotTest {

    @Test
    void shouldCreateAccountSnapshot() {
        AccountSnapshot snapshot = new AccountSnapshot(
                new AccountId(UUID.randomUUID()),
                new CustomerId(UUID.randomUUID()),
                Currency.PEN,
                new BigDecimal("500.00"),
                true
        );

        assertNotNull(snapshot.getAccountId());
        assertNotNull(snapshot.getCustomerId());
        assertEquals(Currency.PEN, snapshot.getCurrency());
        assertEquals(new BigDecimal("500.00"), snapshot.getBalance());
        assertTrue(snapshot.isActive());
    }

    @Test
    void shouldAllowTransferWhenActiveSameCurrencyAndEnoughBalance() {
        AccountSnapshot snapshot = new AccountSnapshot(
                new AccountId(UUID.randomUUID()),
                new CustomerId(UUID.randomUUID()),
                Currency.PEN,
                new BigDecimal("500.00"),
                true
        );

        Money amount = new Money(new BigDecimal("100.00"), Currency.PEN);

        assertDoesNotThrow(() -> snapshot.validateCanTransfer(amount));
    }

    @Test
    void shouldRejectTransferWhenAccountIsInactive() {
        AccountSnapshot snapshot = new AccountSnapshot(
                new AccountId(UUID.randomUUID()),
                new CustomerId(UUID.randomUUID()),
                Currency.PEN,
                new BigDecimal("500.00"),
                false
        );

        Money amount = new Money(new BigDecimal("100.00"), Currency.PEN);

        assertThrows(IllegalStateException.class,
                () -> snapshot.validateCanTransfer(amount));
    }

    @Test
    void shouldRejectTransferWhenCurrencyMismatch() {
        AccountSnapshot snapshot = new AccountSnapshot(
                new AccountId(UUID.randomUUID()),
                new CustomerId(UUID.randomUUID()),
                Currency.PEN,
                new BigDecimal("500.00"),
                true
        );

        Money amount = new Money(new BigDecimal("100.00"), Currency.USD);

        assertThrows(IllegalArgumentException.class,
                () -> snapshot.validateCanTransfer(amount));
    }

    @Test
    void shouldRejectTransferWhenInsufficientBalance() {
        AccountSnapshot snapshot = new AccountSnapshot(
                new AccountId(UUID.randomUUID()),
                new CustomerId(UUID.randomUUID()),
                Currency.PEN,
                new BigDecimal("50.00"),
                true
        );

        Money amount = new Money(new BigDecimal("100.00"), Currency.PEN);

        assertThrows(IllegalArgumentException.class,
                () -> snapshot.validateCanTransfer(amount));
    }
}