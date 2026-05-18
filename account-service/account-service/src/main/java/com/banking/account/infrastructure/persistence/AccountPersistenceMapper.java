package com.banking.account.infrastructure.persistence;

import com.banking.account.domain.model.*;

public class AccountPersistenceMapper {

    public Account toDomain(AccountJpaEntity entity) {
        return Account.restore(
                AccountId.of(entity.getId()),
                CustomerId.of(entity.getCustomerId()),
                entity.getAccountNumber(),
                entity.getAccountType(),
                entity.getStatus(),
                new Money(entity.getBalance(), entity.getCurrency()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public AccountJpaEntity toEntity(Account account) {
        AccountJpaEntity entity = new AccountJpaEntity();

        entity.setId(account.getId().value());
        entity.setCustomerId(account.getCustomerId().value());
        entity.setAccountNumber(account.getAccountNumber());
        entity.setAccountType(account.getType());
        entity.setStatus(account.getStatus());
        entity.setBalance(account.getBalance().amount());
        entity.setCurrency(account.getCurrency());
        entity.setCreatedAt(account.getCreatedAt());
        entity.setUpdatedAt(account.getUpdatedAt());

        return entity;
    }

    public void updateEntity(Account account, AccountJpaEntity entity) {
        entity.setCustomerId(account.getCustomerId().value());
        entity.setAccountNumber(account.getAccountNumber());
        entity.setAccountType(account.getType());
        entity.setStatus(account.getStatus());
        entity.setBalance(account.getBalance().amount());
        entity.setCurrency(account.getCurrency());
        entity.setCreatedAt(account.getCreatedAt());
        entity.setUpdatedAt(account.getUpdatedAt());
    }
}