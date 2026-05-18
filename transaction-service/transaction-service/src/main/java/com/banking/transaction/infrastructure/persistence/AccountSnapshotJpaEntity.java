package com.banking.transaction.infrastructure.persistence;

import com.banking.transaction.domain.model.Currency;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "account_snapshots")
public class AccountSnapshotJpaEntity {

    @Id
    @Column(name = "account_id")
    private UUID accountId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected AccountSnapshotJpaEntity() {
    }

    public AccountSnapshotJpaEntity(
            UUID accountId,
            UUID customerId,
            Currency currency,
            BigDecimal balance,
            boolean active,
            Instant updatedAt
    ) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.currency = currency;
        this.balance = balance;
        this.active = active;
        this.updatedAt = updatedAt;
    }

    public UUID getAccountId() { return accountId; }
    public UUID getCustomerId() { return customerId; }
    public Currency getCurrency() { return currency; }
    public BigDecimal getBalance() { return balance; }
    public boolean isActive() { return active; }
    public Instant getUpdatedAt() { return updatedAt; }
}