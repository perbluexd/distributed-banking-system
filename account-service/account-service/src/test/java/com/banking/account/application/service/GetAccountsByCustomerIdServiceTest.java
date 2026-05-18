package com.banking.account.application.service;

import com.banking.account.application.error.ValidationException;
import com.banking.account.application.port.out.AccountRepositoryPort;
import com.banking.account.application.query.GetAccountsByCustomerIdQuery;
import com.banking.account.domain.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetAccountsByCustomerIdServiceTest {

    private final AccountRepositoryPort accountRepositoryPort = mock(AccountRepositoryPort.class);
    private final GetAccountsByCustomerIdService service =
            new GetAccountsByCustomerIdService(accountRepositoryPort);

    @Test
    void shouldGetAccountsByCustomerIdSuccessfully() {
        UUID customerUuid = UUID.randomUUID();
        CustomerId customerId = CustomerId.of(customerUuid);

        Account firstAccount = Account.create(customerId, "ACC-111", AccountType.SAVINGS, Currency.PEN);
        Account secondAccount = Account.create(customerId, "ACC-222", AccountType.CHECKING, Currency.USD);

        when(accountRepositoryPort.findByCustomerId(customerId))
                .thenReturn(List.of(firstAccount, secondAccount));

        List<Account> result = service.getAccountsByCustomerId(
                new GetAccountsByCustomerIdQuery(customerUuid)
        );

        assertEquals(2, result.size());
        verify(accountRepositoryPort).findByCustomerId(customerId);
    }

    @Test
    void shouldReturnEmptyListWhenCustomerHasNoAccounts() {
        UUID customerUuid = UUID.randomUUID();
        CustomerId customerId = CustomerId.of(customerUuid);

        when(accountRepositoryPort.findByCustomerId(customerId)).thenReturn(List.of());

        List<Account> result = service.getAccountsByCustomerId(
                new GetAccountsByCustomerIdQuery(customerUuid)
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowValidationExceptionWhenQueryIsNull() {
        assertThrows(ValidationException.class, () -> service.getAccountsByCustomerId(null));
    }

    @Test
    void shouldThrowValidationExceptionWhenCustomerIdIsNull() {
        assertThrows(
                ValidationException.class,
                () -> service.getAccountsByCustomerId(new GetAccountsByCustomerIdQuery(null))
        );
    }
}