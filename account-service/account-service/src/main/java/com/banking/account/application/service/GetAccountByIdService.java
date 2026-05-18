package com.banking.account.application.service;

import com.banking.account.application.error.ErrorCode;
import com.banking.account.application.error.NotFoundException;
import com.banking.account.application.error.ValidationException;
import com.banking.account.application.port.in.GetAccountByIdUseCase;
import com.banking.account.application.port.out.AccountRepositoryPort;
import com.banking.account.application.query.GetAccountByIdQuery;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.AccountId;

public class GetAccountByIdService implements GetAccountByIdUseCase {

    private final AccountRepositoryPort accountRepositoryPort;

    public GetAccountByIdService(AccountRepositoryPort accountRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
    }

    @Override
    public Account getAccountById(GetAccountByIdQuery query) {
        if (query == null || query.accountId() == null) {
            throw new ValidationException(
                    ErrorCode.VALIDATION_ERROR,
                    "Account id is required"
            );
        }

        AccountId accountId = AccountId.of(query.accountId());

        return accountRepositoryPort.findById(accountId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.ACCOUNT_NOT_FOUND,
                        "Account not found"
                ));
    }
}