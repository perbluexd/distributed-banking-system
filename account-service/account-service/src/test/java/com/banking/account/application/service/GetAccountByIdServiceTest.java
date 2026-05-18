package com.banking.account.application.service;

import com.banking.account.application.error.NotFoundException;
import com.banking.account.application.error.ValidationException;
import com.banking.account.application.port.out.AccountRepositoryPort;
import com.banking.account.application.query.GetAccountByIdQuery;
import com.banking.account.domain.model.*;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetAccountByIdServiceTest {

    private final AccountRepositoryPort accountRepositoryPort = mock(AccountRepositoryPort.class);
    private final GetAccountByIdService service = new GetAccountByIdService(accountRepositoryPort);

    @Test
    void shouldGetAccountByIdSuccessfully() {
        UUID accountUuid = UUID.randomUUID();
        AccountId accountId = AccountId.of(accountUuid);

        Account account = Account.create(
                CustomerId.of(UUID.randomUUID()),
                "ACC-123456789",
                AccountType.SAVINGS,
                Currency.PEN
        );

        when(accountRepositoryPort.findById(accountId)).thenReturn(Optional.of(account));

        Account result = service.getAccountById(new GetAccountByIdQuery(accountUuid));

        assertNotNull(result);
        assertEquals(account, result);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenAccountDoesNotExist() {
        UUID accountUuid = UUID.randomUUID();
        AccountId accountId = AccountId.of(accountUuid);

        when(accountRepositoryPort.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.getAccountById(new GetAccountByIdQuery(accountUuid))
        );
    }

    @Test
    void shouldThrowValidationExceptionWhenQueryIsNull() {
        assertThrows(ValidationException.class, () -> service.getAccountById(null));
    }

    @Test
    void shouldThrowValidationExceptionWhenAccountIdIsNull() {
        assertThrows(
                ValidationException.class,
                () -> service.getAccountById(new GetAccountByIdQuery(null))
        );
    }
}