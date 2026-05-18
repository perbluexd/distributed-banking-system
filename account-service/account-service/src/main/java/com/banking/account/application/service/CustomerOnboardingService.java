package com.banking.account.application.service;

import com.banking.account.application.command.CreateAccountCommand;
import com.banking.account.application.port.in.CreateAccountUseCase;
import com.banking.account.application.port.in.GetAccountsByCustomerIdUseCase;
import com.banking.account.application.query.GetAccountsByCustomerIdQuery;
import com.banking.account.domain.model.Account;
import com.banking.account.domain.model.AccountType;
import com.banking.account.domain.model.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerOnboardingService {

    private static final Logger log = LoggerFactory.getLogger(CustomerOnboardingService.class);

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountsByCustomerIdUseCase getAccountsByCustomerIdUseCase;

    public CustomerOnboardingService(
            CreateAccountUseCase createAccountUseCase,
            GetAccountsByCustomerIdUseCase getAccountsByCustomerIdUseCase
    ) {
        this.createAccountUseCase = createAccountUseCase;
        this.getAccountsByCustomerIdUseCase = getAccountsByCustomerIdUseCase;
    }

    public void onboardCustomer(UUID customerId) {

        log.info(
                "[ACCOUNT] Starting customer onboarding. customerId={}",
                customerId
        );

        List<Account> existingAccounts =
                getAccountsByCustomerIdUseCase.getAccountsByCustomerId(
                        new GetAccountsByCustomerIdQuery(customerId)
                );

        if (!existingAccounts.isEmpty()) {

            log.info(
                    "[ACCOUNT] Customer already has accounts. customerId={}, existingAccounts={}",
                    customerId,
                    existingAccounts.size()
            );

            return;
        }

        CreateAccountCommand command = new CreateAccountCommand(
                customerId,
                AccountType.SAVINGS,
                Currency.PEN
        );

        Account createdAccount = createAccountUseCase.createAccount(command);

        log.info(
                "[ACCOUNT] Default account created automatically. customerId={}, accountId={}, accountType={}, currency={}",
                customerId,
                createdAccount.getId(),
                AccountType.SAVINGS,
                Currency.PEN
        );
    }
}