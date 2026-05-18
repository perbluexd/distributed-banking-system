package com.banking.account.api.mapper;

import com.banking.account.api.dto.AccountMoneyOperationRequest;
import com.banking.account.api.dto.AccountMoneyOperationResponse;
import com.banking.account.api.dto.AccountResponse;
import com.banking.account.api.dto.ChangeAccountStatusResponse;
import com.banking.account.api.dto.CreateAccountRequest;
import com.banking.account.application.command.CreditAccountCommand;
import com.banking.account.application.command.CreateAccountCommand;
import com.banking.account.application.command.DebitAccountCommand;
import com.banking.account.domain.model.Account;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountApiMapper {

    public CreateAccountCommand toCommand(CreateAccountRequest request) {
        return new CreateAccountCommand(
                request.customerId(),
                request.accountType(),
                request.currency()
        );
    }

    public DebitAccountCommand toDebitCommand(
            UUID accountId,
            AccountMoneyOperationRequest request
    ) {
        return new DebitAccountCommand(
                accountId,
                request.amount(),
                request.currency(),
                request.transferId()
        );
    }

    public CreditAccountCommand toCreditCommand(
            UUID accountId,
            AccountMoneyOperationRequest request
    ) {
        return new CreditAccountCommand(
                accountId,
                request.amount(),
                request.currency(),
                request.transferId()
        );
    }

    public AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId().value(),
                account.getCustomerId().value(),
                account.getAccountNumber(),
                account.getType(),
                account.getStatus(),
                account.getBalance().amount(),
                account.getCurrency(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }

    public ChangeAccountStatusResponse toStatusResponse(Account account, String message) {
        return new ChangeAccountStatusResponse(
                account.getId().value(),
                account.getStatus(),
                message
        );
    }

    public AccountMoneyOperationResponse toMoneyOperationResponse(
            Account account,
            UUID transferId,
            String message
    ) {
        return new AccountMoneyOperationResponse(
                account.getId().value(),
                transferId,
                account.getBalance().amount(),
                account.getCurrency(),
                message
        );
    }
}