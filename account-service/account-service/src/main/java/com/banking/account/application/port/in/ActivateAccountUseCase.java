package com.banking.account.application.port.in;

import com.banking.account.application.command.ActivateAccountCommand;
import com.banking.account.domain.model.Account;

public interface ActivateAccountUseCase {

    Account activateAccount(ActivateAccountCommand command);
}