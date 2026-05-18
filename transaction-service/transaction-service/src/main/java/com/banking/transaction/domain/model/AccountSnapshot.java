package com.banking.transaction.domain.model;

import java.math.BigDecimal;

public class AccountSnapshot {

    private final AccountId accountId;
    private final CustomerId customerId;
    private final Currency currency;
    private final BigDecimal balance;
    private final boolean active;

    public AccountSnapshot(
            AccountId accountId,
            CustomerId customerId,
            Currency currency,
            BigDecimal balance,
            boolean active
    ) {
        if (accountId == null) throw new IllegalArgumentException("AccountId required");
        if (customerId == null) throw new IllegalArgumentException("CustomerId required");
        if (currency == null) throw new IllegalArgumentException("Currency required");
        if (balance == null) throw new IllegalArgumentException("Balance required");

        this.accountId = accountId;
        this.customerId = customerId;
        this.currency = currency;
        this.balance = balance;
        this.active = active;
    }

    public void validateCanTransfer(Money amount) {
        if (!active) {
            throw new IllegalStateException("Account is not active");
        }

        if (!currency.equals(amount.currency())) {
            throw new IllegalArgumentException("Currency mismatch");
        }

        if (balance.compareTo(amount.amount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
    }

    public AccountId getAccountId() {
        return accountId;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }
}