package com.banking.transaction.application.port.out;

import com.banking.transaction.application.command.CreditAccountCommand;
import com.banking.transaction.application.command.DebitAccountCommand;

public interface AccountCommandPort {

    void debit(DebitAccountCommand command);

    void credit(CreditAccountCommand command);
}