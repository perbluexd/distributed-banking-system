package com.banking.account.application.service;

import com.banking.account.application.error.ErrorCode;
import com.banking.account.application.error.ValidationException;
import com.banking.account.application.port.in.GetAccountsByCustomerIdUseCase;
import com.banking.account.application.port.out.AccountRepositoryPort;
import com.banking.account.application.query.GetAccountsByCustomerIdQuery;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.CustomerId;

import java.util.List;

public class GetAccountsByCustomerIdService implements GetAccountsByCustomerIdUseCase {

    private final AccountRepositoryPort accountRepositoryPort;

    public GetAccountsByCustomerIdService(AccountRepositoryPort accountRepositoryPort) {
        this.accountRepositoryPort = accountRepositoryPort;
    }

    @Override
    public List<Account> getAccountsByCustomerId(GetAccountsByCustomerIdQuery query) {
        if (query == null || query.customerId() == null) {
            throw new ValidationException(
                    ErrorCode.VALIDATION_ERROR,
                    "Customer id is required"
            );
        }

        CustomerId customerId = CustomerId.of(query.customerId());

        return accountRepositoryPort.findByCustomerId(customerId);
    }
}