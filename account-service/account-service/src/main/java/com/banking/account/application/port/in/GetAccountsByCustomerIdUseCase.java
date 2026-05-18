package com.banking.account.application.port.in;

import com.banking.account.application.query.GetAccountsByCustomerIdQuery;
import com.banking.account.domain.model.Account;

import java.util.List;

public interface GetAccountsByCustomerIdUseCase {

    List<Account> getAccountsByCustomerId(GetAccountsByCustomerIdQuery query);
}