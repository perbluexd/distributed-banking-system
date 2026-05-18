package com.banking.transaction.infrastructure.persistence;

import com.banking.transaction.application.port.out.AccountSnapshotRepositoryPort;
import com.banking.transaction.domain.model.AccountId;
import com.banking.transaction.domain.model.AccountSnapshot;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AccountSnapshotPersistenceAdapter implements AccountSnapshotRepositoryPort {

    private final AccountSnapshotJpaRepository accountSnapshotJpaRepository;

    public AccountSnapshotPersistenceAdapter(AccountSnapshotJpaRepository accountSnapshotJpaRepository) {
        this.accountSnapshotJpaRepository = accountSnapshotJpaRepository;
    }

    @Override
    public Optional<AccountSnapshot> findByAccountId(AccountId accountId) {
        return accountSnapshotJpaRepository.findById(accountId.value())
                .map(AccountSnapshotPersistenceMapper::toDomain);
    }

    @Override
    public AccountSnapshot save(AccountSnapshot snapshot) {
        AccountSnapshotJpaEntity entity = AccountSnapshotPersistenceMapper.toEntity(snapshot);
        AccountSnapshotJpaEntity saved = accountSnapshotJpaRepository.save(entity);
        return AccountSnapshotPersistenceMapper.toDomain(saved);
    }
}