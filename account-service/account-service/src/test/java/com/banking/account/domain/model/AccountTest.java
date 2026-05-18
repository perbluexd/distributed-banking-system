package com.banking.account.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void shouldCreateAccountSuccessfully() {
        CustomerId customerId = CustomerId.of(java.util.UUID.randomUUID());

        Account account = Account.create(
                customerId,
                "ACC-123456789",
                AccountType.SAVINGS,
                Currency.PEN
        );

        assertNotNull(account.getId());
        assertEquals(customerId, account.getCustomerId());
        assertEquals("ACC-123456789", account.getAccountNumber());
        assertEquals(AccountType.SAVINGS, account.getType());
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        assertEquals(Currency.PEN, account.getCurrency());
        assertEquals(0, account.getBalance().amount().compareTo(java.math.BigDecimal.ZERO.setScale(2)));
        assertNotNull(account.getCreatedAt());
        assertNotNull(account.getUpdatedAt());
    }

    @Test
    void shouldBlockActiveAccount() {
        Account account = Account.create(
                CustomerId.of(java.util.UUID.randomUUID()),
                "ACC-123456789",
                AccountType.SAVINGS,
                Currency.PEN
        );

        account.block();

        assertEquals(AccountStatus.BLOCKED, account.getStatus());
    }

    @Test
    void shouldActivateBlockedAccount() {
        Account account = Account.create(
                CustomerId.of(java.util.UUID.randomUUID()),
                "ACC-123456789",
                AccountType.SAVINGS,
                Currency.PEN
        );

        account.block();
        account.activate();

        assertEquals(AccountStatus.ACTIVE, account.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenBlockingAlreadyBlockedAccount() {
        Account account = Account.create(
                CustomerId.of(java.util.UUID.randomUUID()),
                "ACC-123456789",
                AccountType.SAVINGS,
                Currency.PEN
        );

        account.block();

        assertThrows(IllegalStateException.class, account::block);
    }

    @Test
    void shouldThrowExceptionWhenActivatingAlreadyActiveAccount() {
        Account account = Account.create(
                CustomerId.of(java.util.UUID.randomUUID()),
                "ACC-123456789",
                AccountType.SAVINGS,
                Currency.PEN
        );

        assertThrows(IllegalStateException.class, account::activate);
    }
}