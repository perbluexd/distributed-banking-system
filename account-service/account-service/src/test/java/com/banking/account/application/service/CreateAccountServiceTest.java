package com.banking.account.application.service;

import com.banking.account.application.command.CreateAccountCommand;
import com.banking.account.application.error.NotFoundException;
import com.banking.account.application.error.ValidationException;
import com.banking.account.application.port.out.AccountRepositoryPort;
import com.banking.account.application.port.out.CustomerSnapshotRepositoryPort;
import com.banking.account.application.port.out.OutboxEventRepositoryPort;
import com.banking.account.domain.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateAccountServiceTest {

    private final AccountRepositoryPort accountRepositoryPort = mock(AccountRepositoryPort.class);
    private final CustomerSnapshotRepositoryPort customerSnapshotRepositoryPort = mock(CustomerSnapshotRepositoryPort.class);
    private final OutboxEventRepositoryPort outboxEventRepositoryPort = mock(OutboxEventRepositoryPort.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private final CreateAccountService service = new CreateAccountService(
            accountRepositoryPort,
            customerSnapshotRepositoryPort,
            outboxEventRepositoryPort,
            objectMapper
    );

    @Test
    void shouldCreateAccountSuccessfully() {
        UUID customerUuid = UUID.randomUUID();

        CreateAccountCommand command = new CreateAccountCommand(
                customerUuid,
                AccountType.SAVINGS,
                Currency.PEN
        );

        when(customerSnapshotRepositoryPort.existsByCustomerId(CustomerId.of(customerUuid))).thenReturn(true);
        when(accountRepositoryPort.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepositoryPort.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = service.createAccount(command);

        assertNotNull(result);
        assertEquals(CustomerId.of(customerUuid), result.getCustomerId());
        assertEquals(AccountType.SAVINGS, result.getType());
        assertEquals(AccountStatus.ACTIVE, result.getStatus());
        assertEquals(Currency.PEN, result.getCurrency());

        verify(customerSnapshotRepositoryPort).existsByCustomerId(CustomerId.of(customerUuid));
        verify(accountRepositoryPort).save(any(Account.class));
        verify(outboxEventRepositoryPort).save(any(OutboxEvent.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCustomerSnapshotDoesNotExist() {
        UUID customerUuid = UUID.randomUUID();

        CreateAccountCommand command = new CreateAccountCommand(
                customerUuid,
                AccountType.SAVINGS,
                Currency.PEN
        );

        when(customerSnapshotRepositoryPort.existsByCustomerId(CustomerId.of(customerUuid))).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.createAccount(command));

        verify(customerSnapshotRepositoryPort).existsByCustomerId(CustomerId.of(customerUuid));
        verify(accountRepositoryPort, never()).save(any(Account.class));
        verify(outboxEventRepositoryPort, never()).save(any(OutboxEvent.class));
    }

    @Test
    void shouldThrowValidationExceptionWhenCommandIsNull() {
        assertThrows(ValidationException.class, () -> service.createAccount(null));

        verifyNoInteractions(customerSnapshotRepositoryPort);
        verifyNoInteractions(accountRepositoryPort);
        verifyNoInteractions(outboxEventRepositoryPort);
    }

    @Test
    void shouldThrowValidationExceptionWhenCustomerIdIsNull() {
        CreateAccountCommand command = new CreateAccountCommand(
                null,
                AccountType.SAVINGS,
                Currency.PEN
        );

        assertThrows(ValidationException.class, () -> service.createAccount(command));

        verifyNoInteractions(customerSnapshotRepositoryPort);
        verifyNoInteractions(accountRepositoryPort);
        verifyNoInteractions(outboxEventRepositoryPort);
    }

    @Test
    void shouldThrowValidationExceptionWhenAccountTypeIsNull() {
        CreateAccountCommand command = new CreateAccountCommand(
                UUID.randomUUID(),
                null,
                Currency.PEN
        );

        assertThrows(ValidationException.class, () -> service.createAccount(command));

        verifyNoInteractions(customerSnapshotRepositoryPort);
        verifyNoInteractions(accountRepositoryPort);
        verifyNoInteractions(outboxEventRepositoryPort);
    }

    @Test
    void shouldThrowValidationExceptionWhenCurrencyIsNull() {
        CreateAccountCommand command = new CreateAccountCommand(
                UUID.randomUUID(),
                AccountType.SAVINGS,
                null
        );

        assertThrows(ValidationException.class, () -> service.createAccount(command));

        verifyNoInteractions(customerSnapshotRepositoryPort);
        verifyNoInteractions(accountRepositoryPort);
        verifyNoInteractions(outboxEventRepositoryPort);
    }

    @Test
    void shouldSaveGeneratedAccountNumber() {
        UUID customerUuid = UUID.randomUUID();

        CreateAccountCommand command = new CreateAccountCommand(
                customerUuid,
                AccountType.CHECKING,
                Currency.USD
        );

        when(customerSnapshotRepositoryPort.existsByCustomerId(CustomerId.of(customerUuid))).thenReturn(true);
        when(accountRepositoryPort.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepositoryPort.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.createAccount(command);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepositoryPort).save(captor.capture());

        Account savedAccount = captor.getValue();

        assertNotNull(savedAccount.getAccountNumber());
        assertTrue(savedAccount.getAccountNumber().startsWith("ACC-"));
        assertEquals(16, savedAccount.getAccountNumber().length());

        verify(outboxEventRepositoryPort).save(any(OutboxEvent.class));
    }
}