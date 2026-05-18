package com.banking.account.infrastructure.persistence;

import com.banking.account.application.port.out.AccountRepositoryPort;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.AccountId;
import com.banking.account.domain.model.CustomerId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AccountPersistenceAdapter implements AccountRepositoryPort {

    private final AccountJpaRepository accountJpaRepository;
    private final AccountPersistenceMapper accountPersistenceMapper;

    public AccountPersistenceAdapter(
            AccountJpaRepository accountJpaRepository,
            AccountPersistenceMapper accountPersistenceMapper
    ) {
        this.accountJpaRepository = accountJpaRepository;
        this.accountPersistenceMapper = accountPersistenceMapper;
    }

    @Override
    public Account save(Account account) {
        AccountJpaEntity entity = accountJpaRepository.findById(account.getId().value())
                .map(existingEntity -> {
                    accountPersistenceMapper.updateEntity(account, existingEntity);
                    return existingEntity;
                })
                .orElseGet(() -> accountPersistenceMapper.toEntity(account));

        AccountJpaEntity savedEntity = accountJpaRepository.save(entity);
        return accountPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Account> findById(AccountId accountId) {
        return accountJpaRepository.findById(accountId.value())
                .map(accountPersistenceMapper::toDomain);
    }

    @Override
    public List<Account> findByCustomerId(CustomerId customerId) {
        return accountJpaRepository.findByCustomerId(customerId.value())
                .stream()
                .map(accountPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return accountJpaRepository.existsByAccountNumber(accountNumber);
    }
}