package com.banking.account.application.port.in;

import com.banking.account.application.query.GetAccountByIdQuery;
import com.banking.account.domain.model.Account;

public interface GetAccountByIdUseCase {

    Account getAccountById(GetAccountByIdQuery query);
}