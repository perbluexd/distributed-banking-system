package com.banking.transaction.application.port.out;

import com.banking.transaction.domain.model.AccountId;
import com.banking.transaction.domain.model.AccountSnapshot;

import java.util.Optional;

public interface AccountSnapshotRepositoryPort {

    Optional<AccountSnapshot> findByAccountId(AccountId accountId);

    AccountSnapshot save(AccountSnapshot snapshot);
}