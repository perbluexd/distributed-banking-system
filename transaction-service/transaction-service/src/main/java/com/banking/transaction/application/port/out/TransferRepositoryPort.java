package com.banking.transaction.application.port.out;

import com.banking.transaction.domain.model.AccountId;
import com.banking.transaction.domain.model.Transfer;
import com.banking.transaction.domain.model.TransferId;

import java.util.List;
import java.util.Optional;

public interface TransferRepositoryPort {

    Transfer save(Transfer transfer);

    Optional<Transfer> findById(TransferId transferId);

    List<Transfer> findByAccountId(AccountId accountId);
}