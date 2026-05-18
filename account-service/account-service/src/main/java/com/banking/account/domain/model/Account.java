package com.banking.account.domain.model;

import java.time.Instant;

public class Account {

    private final AccountId id;
    private final CustomerId customerId;
    private final String accountNumber;
    private final AccountType type;
    private AccountStatus status;
    private Money balance;
    private final Instant createdAt;
    private Instant updatedAt;

    private Account(
            AccountId id,
            CustomerId customerId,
            String accountNumber,
            AccountType type,
            AccountStatus status,
            Money balance,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.type = type;
        this.status = status;
        this.balance = balance;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        validate();
    }

    public static Account create(
            CustomerId customerId,
            String accountNumber,
            AccountType type,
            Currency currency
    ) {
        Instant now = Instant.now();

        return new Account(
                AccountId.newId(),
                customerId,
                accountNumber,
                type,
                AccountStatus.ACTIVE,
                Money.zero(currency),
                now,
                now
        );
    }

    public static Account restore(
            AccountId id,
            CustomerId customerId,
            String accountNumber,
            AccountType type,
            AccountStatus status,
            Money balance,
            Instant createdAt,
            Instant updatedAt
    ) {
        return new Account(
                id,
                customerId,
                accountNumber,
                type,
                status,
                balance,
                createdAt,
                updatedAt
        );
    }

    public void debit(Money amount) {
        validateAccountIsActive();
        validateMoney(amount);

        this.balance = this.balance.subtract(amount);
        this.updatedAt = Instant.now();
    }

    public void credit(Money amount) {
        validateAccountIsActive();
        validateMoney(amount);

        this.balance = this.balance.add(amount);
        this.updatedAt = Instant.now();
    }

    public void block() {
        if (this.status == AccountStatus.CLOSED) {
            throw new IllegalStateException("Closed account cannot be blocked");
        }

        if (this.status == AccountStatus.BLOCKED) {
            throw new IllegalStateException("Account is already blocked");
        }

        this.status = AccountStatus.BLOCKED;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        if (this.status == AccountStatus.CLOSED) {
            throw new IllegalStateException("Closed account cannot be activated");
        }

        if (this.status == AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is already active");
        }

        this.status = AccountStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    private void validateAccountIsActive() {
        if (this.status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Only active accounts can perform money operations");
        }
    }

    private void validateMoney(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Money amount cannot be null");
        }

        if (!this.balance.currency().equals(amount.currency())) {
            throw new IllegalArgumentException("Currencies must be the same");
        }
    }

    private void validate() {
        if (id == null) {
            throw new IllegalArgumentException("Account id cannot be null");
        }

        if (customerId == null) {
            throw new IllegalArgumentException("Customer id cannot be null");
        }

        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number cannot be blank");
        }

        if (type == null) {
            throw new IllegalArgumentException("Account type cannot be null");
        }

        if (status == null) {
            throw new IllegalArgumentException("Account status cannot be null");
        }

        if (balance == null) {
            throw new IllegalArgumentException("Balance cannot be null");
        }

        if (createdAt == null) {
            throw new IllegalArgumentException("CreatedAt cannot be null");
        }

        if (updatedAt == null) {
            throw new IllegalArgumentException("UpdatedAt cannot be null");
        }
    }

    public AccountId getId() {
        return id;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AccountType getType() {
        return type;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public Money getBalance() {
        return balance;
    }

    public Currency getCurrency() {
        return balance.currency();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}