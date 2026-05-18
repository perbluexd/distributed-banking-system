package com.banking.account.application.port.in;

import com.banking.account.application.command.CreditAccountCommand;
import com.banking.account.domain.model.Account;

public interface CreditAccountUseCase {

    Account credit(CreditAccountCommand command);
}