package com.banking.account.application.port.in;

import com.banking.account.application.command.CreateAccountCommand;
import com.banking.account.domain.model.Account;

public interface CreateAccountUseCase {

    Account createAccount(CreateAccountCommand command);
}