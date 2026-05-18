package com.banking.transaction.infrastructure.client;

import com.banking.transaction.application.command.CreditAccountCommand;
import com.banking.transaction.application.command.DebitAccountCommand;
import com.banking.transaction.application.port.out.AccountCommandPort;
import org.springframework.stereotype.Component;

@Component
public class AccountCommandRestAdapter implements AccountCommandPort {

    private final AccountServiceClient accountServiceClient;

    public AccountCommandRestAdapter(AccountServiceClient accountServiceClient) {
        this.accountServiceClient = accountServiceClient;
    }

    @Override
    public void debit(DebitAccountCommand command) {
        accountServiceClient.debit(
                command.accountId(),
                command.amount(),
                command.currency(),
                command.transferId()
        );
    }

    @Override
    public void credit(CreditAccountCommand command) {
        accountServiceClient.credit(
                command.accountId(),
                command.amount(),
                command.currency(),
                command.transferId()
        );
    }
}