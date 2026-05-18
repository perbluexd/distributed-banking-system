package com.banking.account.api.controller;

import com.banking.account.api.mapper.AccountApiMapper;
import com.banking.account.application.port.in.*;
import com.banking.account.domain.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(AccountApiMapper.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateAccountUseCase createAccountUseCase;

    @MockitoBean
    private GetAccountByIdUseCase getAccountByIdUseCase;

    @MockitoBean
    private GetAccountsByCustomerIdUseCase getAccountsByCustomerIdUseCase;

    @MockitoBean
    private BlockAccountUseCase blockAccountUseCase;

    @MockitoBean
    private ActivateAccountUseCase activateAccountUseCase;

    @MockitoBean
    private DebitAccountUseCase debitAccountUseCase;

    @MockitoBean
    private CreditAccountUseCase creditAccountUseCase;

    @Test
    void shouldCreateAccountSuccessfully() throws Exception {
        UUID customerId = UUID.randomUUID();

        Account account = Account.create(
                CustomerId.of(customerId),
                "ACC-123456789",
                AccountType.SAVINGS,
                Currency.PEN
        );

        when(createAccountUseCase.createAccount(any())).thenReturn(account);

        String requestBody = """
                {
                  "customerId": "%s",
                  "accountType": "SAVINGS",
                  "currency": "PEN"
                }
                """.formatted(customerId);

        mockMvc.perform(post("/accounts")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.accountNumber").value("ACC-123456789"))
                .andExpect(jsonPath("$.accountType").value("SAVINGS"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.currency").value("PEN"));

        verify(createAccountUseCase).createAccount(any());
    }

    @Test
    void shouldGetAccountByIdSuccessfully() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Account account = Account.restore(
                AccountId.of(accountId),
                CustomerId.of(customerId),
                "ACC-123456789",
                AccountType.SAVINGS,
                AccountStatus.ACTIVE,
                Money.zero(Currency.PEN),
                java.time.Instant.now(),
                java.time.Instant.now()
        );

        when(getAccountByIdUseCase.getAccountById(any())).thenReturn(account);

        mockMvc.perform(get("/accounts/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.accountNumber").value("ACC-123456789"));

        verify(getAccountByIdUseCase).getAccountById(any());
    }

    @Test
    void shouldGetAccountsByCustomerIdSuccessfully() throws Exception {
        UUID customerId = UUID.randomUUID();

        Account firstAccount = Account.create(
                CustomerId.of(customerId),
                "ACC-111",
                AccountType.SAVINGS,
                Currency.PEN
        );

        Account secondAccount = Account.create(
                CustomerId.of(customerId),
                "ACC-222",
                AccountType.CHECKING,
                Currency.USD
        );

        when(getAccountsByCustomerIdUseCase.getAccountsByCustomerId(any()))
                .thenReturn(List.of(firstAccount, secondAccount));

        mockMvc.perform(get("/accounts/customer/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].accountNumber").value("ACC-111"))
                .andExpect(jsonPath("$[1].accountNumber").value("ACC-222"));

        verify(getAccountsByCustomerIdUseCase).getAccountsByCustomerId(any());
    }

    @Test
    void shouldBlockAccountSuccessfully() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Account account = Account.restore(
                AccountId.of(accountId),
                CustomerId.of(customerId),
                "ACC-123456789",
                AccountType.SAVINGS,
                AccountStatus.BLOCKED,
                Money.zero(Currency.PEN),
                java.time.Instant.now(),
                java.time.Instant.now()
        );

        when(blockAccountUseCase.blockAccount(any())).thenReturn(account);

        mockMvc.perform(patch("/accounts/{accountId}/block", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                .andExpect(jsonPath("$.status").value("BLOCKED"))
                .andExpect(jsonPath("$.message").value("Account blocked successfully"));

        verify(blockAccountUseCase).blockAccount(any());
    }

    @Test
    void shouldActivateAccountSuccessfully() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Account account = Account.restore(
                AccountId.of(accountId),
                CustomerId.of(customerId),
                "ACC-123456789",
                AccountType.SAVINGS,
                AccountStatus.ACTIVE,
                Money.zero(Currency.PEN),
                java.time.Instant.now(),
                java.time.Instant.now()
        );

        when(activateAccountUseCase.activateAccount(any())).thenReturn(account);

        mockMvc.perform(patch("/accounts/{accountId}/activate", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId.toString()))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.message").value("Account activated successfully"));

        verify(activateAccountUseCase).activateAccount(any());
    }

    @Test
    void shouldReturnBadRequestWhenCreateAccountRequestIsInvalid() throws Exception {
        String requestBody = """
                {
                  "accountType": "SAVINGS",
                  "currency": "PEN"
                }
                """;

        mockMvc.perform(post("/accounts")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(createAccountUseCase, never()).createAccount(any());
    }
}