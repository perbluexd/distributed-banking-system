package com.banking.account.application.port.in;

import com.banking.account.application.command.DebitAccountCommand;
import com.banking.account.domain.model.Account;

public interface DebitAccountUseCase {

    Account debit(DebitAccountCommand command);
}