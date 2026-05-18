package com.banking.account.application.port.out;

import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.AccountId;
import com.banking.account.domain.model.CustomerId;

import java.util.List;
import java.util.Optional;

public interface AccountRepositoryPort {

    Account save(Account account);

    Optional<Account> findById(AccountId accountId);

    List<Account> findByCustomerId(CustomerId customerId);

    boolean existsByAccountNumber(String accountNumber);
}