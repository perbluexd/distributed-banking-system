package com.banking.account.application.port.in;

import com.banking.account.application.command.BlockAccountCommand;
import com.banking.account.domain.model.Account;

public interface BlockAccountUseCase {

    Account blockAccount(BlockAccountCommand command);
}