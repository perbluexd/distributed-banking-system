package com.banking.account.application.service;

import com.banking.account.application.command.BlockAccountCommand;
import com.banking.account.application.error.ConflictException;
import com.banking.account.application.error.NotFoundException;
import com.banking.account.application.error.ValidationException;
import com.banking.account.application.port.out.AccountRepositoryPort;
import com.banking.account.application.port.out.OutboxEventRepositoryPort;
import com.banking.account.domain.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlockAccountServiceTest {

    private final AccountRepositoryPort accountRepositoryPort = mock(AccountRepositoryPort.class);
    private final OutboxEventRepositoryPort outboxEventRepositoryPort = mock(OutboxEventRepositoryPort.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private final BlockAccountService service = new BlockAccountService(
            accountRepositoryPort,
            outboxEventRepositoryPort,
            objectMapper
    );

    @Test
    void shouldBlockAccountSuccessfully() {
        UUID accountUuid = UUID.randomUUID();
        AccountId accountId = AccountId.of(accountUuid);

        Account account = Account.restore(
                accountId,
                CustomerId.of(UUID.randomUUID()),
                "ACC-123456789",
                AccountType.SAVINGS,
                AccountStatus.ACTIVE,
                Money.zero(Currency.PEN),
                Instant.now(),
                Instant.now()
        );

        when(accountRepositoryPort.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepositoryPort.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = service.blockAccount(new BlockAccountCommand(accountUuid));

        assertEquals(AccountStatus.BLOCKED, result.getStatus());

        verify(accountRepositoryPort).save(account);
        verify(outboxEventRepositoryPort).save(any(OutboxEvent.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenAccountDoesNotExist() {
        UUID accountUuid = UUID.randomUUID();
        AccountId accountId = AccountId.of(accountUuid);

        when(accountRepositoryPort.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> service.blockAccount(new BlockAccountCommand(accountUuid))
        );

        verify(accountRepositoryPort, never()).save(any(Account.class));
        verify(outboxEventRepositoryPort, never()).save(any(OutboxEvent.class));
    }

    @Test
    void shouldThrowConflictExceptionWhenAccountIsAlreadyBlocked() {
        UUID accountUuid = UUID.randomUUID();
        AccountId accountId = AccountId.of(accountUuid);

        Account account = Account.restore(
                accountId,
                CustomerId.of(UUID.randomUUID()),
                "ACC-123456789",
                AccountType.SAVINGS,
                AccountStatus.BLOCKED,
                Money.zero(Currency.PEN),
                Instant.now(),
                Instant.now()
        );

        when(accountRepositoryPort.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(
                ConflictException.class,
                () -> service.blockAccount(new BlockAccountCommand(accountUuid))
        );

        verify(accountRepositoryPort, never()).save(any(Account.class));
        verify(outboxEventRepositoryPort, never()).save(any(OutboxEvent.class));
    }

    @Test
    void shouldThrowValidationExceptionWhenCommandIsNull() {
        assertThrows(
                ValidationException.class,
                () -> service.blockAccount(null)
        );

        verify(accountRepositoryPort, never()).findById(any(AccountId.class));
        verify(accountRepositoryPort, never()).save(any(Account.class));
        verify(outboxEventRepositoryPort, never()).save(any(OutboxEvent.class));
    }

    @Test
    void shouldThrowValidationExceptionWhenAccountIdIsNull() {
        assertThrows(
                ValidationException.class,
                () -> service.blockAccount(new BlockAccountCommand(null))
        );

        verify(accountRepositoryPort, never()).findById(any(AccountId.class));
        verify(accountRepositoryPort, never()).save(any(Account.class));
        verify(outboxEventRepositoryPort, never()).save(any(OutboxEvent.class));
    }
}