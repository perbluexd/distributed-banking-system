package com.banking.account.api.controller;

import com.banking.account.api.dto.AccountMoneyOperationRequest;
import com.banking.account.api.dto.AccountMoneyOperationResponse;
import com.banking.account.api.dto.AccountResponse;
import com.banking.account.api.dto.ChangeAccountStatusResponse;
import com.banking.account.api.dto.CreateAccountRequest;
import com.banking.account.api.mapper.AccountApiMapper;
import com.banking.account.application.command.ActivateAccountCommand;
import com.banking.account.application.command.BlockAccountCommand;
import com.banking.account.application.port.in.*;
import com.banking.account.application.query.GetAccountByIdQuery;
import com.banking.account.application.query.GetAccountsByCustomerIdQuery;
import com.banking.account.domain.model.Account;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountByIdUseCase getAccountByIdUseCase;
    private final GetAccountsByCustomerIdUseCase getAccountsByCustomerIdUseCase;
    private final BlockAccountUseCase blockAccountUseCase;
    private final ActivateAccountUseCase activateAccountUseCase;
    private final DebitAccountUseCase debitAccountUseCase;
    private final CreditAccountUseCase creditAccountUseCase;
    private final AccountApiMapper accountApiMapper;

    public AccountController(
            CreateAccountUseCase createAccountUseCase,
            GetAccountByIdUseCase getAccountByIdUseCase,
            GetAccountsByCustomerIdUseCase getAccountsByCustomerIdUseCase,
            BlockAccountUseCase blockAccountUseCase,
            ActivateAccountUseCase activateAccountUseCase,
            DebitAccountUseCase debitAccountUseCase,
            CreditAccountUseCase creditAccountUseCase,
            AccountApiMapper accountApiMapper
    ) {
        this.createAccountUseCase = createAccountUseCase;
        this.getAccountByIdUseCase = getAccountByIdUseCase;
        this.getAccountsByCustomerIdUseCase = getAccountsByCustomerIdUseCase;
        this.blockAccountUseCase = blockAccountUseCase;
        this.activateAccountUseCase = activateAccountUseCase;
        this.debitAccountUseCase = debitAccountUseCase;
        this.creditAccountUseCase = creditAccountUseCase;
        this.accountApiMapper = accountApiMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request) {
        Account account = createAccountUseCase.createAccount(
                accountApiMapper.toCommand(request)
        );

        return accountApiMapper.toResponse(account);
    }

    @GetMapping("/{accountId}")
    public AccountResponse getAccountById(@PathVariable UUID accountId) {
        Account account = getAccountByIdUseCase.getAccountById(
                new GetAccountByIdQuery(accountId)
        );

        return accountApiMapper.toResponse(account);
    }

    @GetMapping("/customer/{customerId}")
    public List<AccountResponse> getAccountsByCustomerId(@PathVariable UUID customerId) {
        return getAccountsByCustomerIdUseCase.getAccountsByCustomerId(
                        new GetAccountsByCustomerIdQuery(customerId)
                )
                .stream()
                .map(accountApiMapper::toResponse)
                .toList();
    }

    @PatchMapping("/{accountId}/block")
    public ChangeAccountStatusResponse blockAccount(@PathVariable UUID accountId) {
        Account account = blockAccountUseCase.blockAccount(
                new BlockAccountCommand(accountId)
        );

        return accountApiMapper.toStatusResponse(account, "Account blocked successfully");
    }

    @PatchMapping("/{accountId}/activate")
    public ChangeAccountStatusResponse activateAccount(@PathVariable UUID accountId) {
        Account account = activateAccountUseCase.activateAccount(
                new ActivateAccountCommand(accountId)
        );

        return accountApiMapper.toStatusResponse(account, "Account activated successfully");
    }

    @PostMapping("/{accountId}/debit")
    public AccountMoneyOperationResponse debitAccount(
            @PathVariable UUID accountId,
            @Valid @RequestBody AccountMoneyOperationRequest request
    ) {
        Account account = debitAccountUseCase.debit(
                accountApiMapper.toDebitCommand(accountId, request)
        );

        return accountApiMapper.toMoneyOperationResponse(
                account,
                request.transferId(),
                "Account debited successfully"
        );
    }

    @PostMapping("/{accountId}/credit")
    public AccountMoneyOperationResponse creditAccount(
            @PathVariable UUID accountId,
            @Valid @RequestBody AccountMoneyOperationRequest request
    ) {
        Account account = creditAccountUseCase.credit(
                accountApiMapper.toCreditCommand(accountId, request)
        );

        return accountApiMapper.toMoneyOperationResponse(
                account,
                request.transferId(),
                "Account credited successfully"
        );
    }
}