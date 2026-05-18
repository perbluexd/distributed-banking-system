package com.banking.transaction.application.service;

import com.banking.transaction.application.port.out.AccountCommandPort;
import com.banking.transaction.application.port.out.AccountSnapshotRepositoryPort;
import com.banking.transaction.application.port.out.OutboxEventRepositoryPort;
import com.banking.transaction.application.port.out.TransferRepositoryPort;
import com.banking.transaction.domain.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

class AccountEventHandlerServiceTest {

    private final AccountSnapshotRepositoryPort accountSnapshotRepositoryPort = mock(AccountSnapshotRepositoryPort.class);
    private final TransferRepositoryPort transferRepositoryPort = mock(TransferRepositoryPort.class);
    private final AccountCommandPort accountCommandPort = mock(AccountCommandPort.class);
    private final OutboxEventRepositoryPort outboxEventRepositoryPort = mock(OutboxEventRepositoryPort.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private final AccountEventHandlerService service = new AccountEventHandlerService(
            accountSnapshotRepositoryPort,
            transferRepositoryPort,
            accountCommandPort,
            outboxEventRepositoryPort,
            objectMapper
    );

    @Test
    void shouldCreateAccountSnapshotWhenAccountCreatedEventArrives() {
        service.handleAccountCreated(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Currency.PEN,
                new BigDecimal("100.00")
        );

        verify(accountSnapshotRepositoryPort).save(any(AccountSnapshot.class));
    }

    @Test
    void shouldUpdateSnapshotWhenAccountBlockedEventArrives() {
        UUID accountId = UUID.randomUUID();

        AccountSnapshot current = new AccountSnapshot(
                new AccountId(accountId),
                new CustomerId(UUID.randomUUID()),
                Currency.PEN,
                new BigDecimal("100.00"),
                true
        );

        when(accountSnapshotRepositoryPort.findByAccountId(new AccountId(accountId)))
                .thenReturn(Optional.of(current));

        service.handleAccountBlocked(accountId);

        verify(accountSnapshotRepositoryPort).save(any(AccountSnapshot.class));
    }

    @Test
    void shouldMarkTransferCompletedAndSaveOutboxWhenAccountCreditedEventArrives() {
        UUID transferId = UUID.randomUUID();
        UUID sourceAccountId = UUID.randomUUID();
        UUID targetAccountId = UUID.randomUUID();
        UUID sourceCustomerId = UUID.randomUUID();
        UUID targetCustomerId = UUID.randomUUID();

        Transfer transfer = Transfer.restore(
                new TransferId(transferId),
                new AccountId(sourceAccountId),
                new AccountId(targetAccountId),
                new Money(new BigDecimal("100.00"), Currency.PEN),
                TransferType.INTERNAL,
                new IdempotencyKey("key-123"),
                TransferStatus.PENDING,
                java.time.Instant.now(),
                java.time.Instant.now()
        );

        AccountSnapshot sourceSnapshot = new AccountSnapshot(
                new AccountId(sourceAccountId),
                new CustomerId(sourceCustomerId),
                Currency.PEN,
                new BigDecimal("900.00"),
                true
        );

        when(transferRepositoryPort.findById(new TransferId(transferId)))
                .thenReturn(Optional.of(transfer));

        when(transferRepositoryPort.save(any(Transfer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(accountSnapshotRepositoryPort.findByAccountId(new AccountId(sourceAccountId)))
                .thenReturn(Optional.of(sourceSnapshot));

        service.handleAccountCredited(
                transferId,
                targetAccountId,
                targetCustomerId,
                Currency.PEN,
                new BigDecimal("300.00")
        );

        verify(accountSnapshotRepositoryPort).save(any(AccountSnapshot.class));
        verify(transferRepositoryPort).save(any(Transfer.class));
        verify(outboxEventRepositoryPort).save(any(OutboxEvent.class));
        verify(accountSnapshotRepositoryPort).findByAccountId(new AccountId(sourceAccountId));
    }

    @Test
    void shouldMarkTransferFailedAndSaveOutbox() {
        UUID transferId = UUID.randomUUID();
        UUID sourceAccountId = UUID.randomUUID();
        UUID targetAccountId = UUID.randomUUID();
        UUID sourceCustomerId = UUID.randomUUID();

        Transfer transfer = Transfer.restore(
                new TransferId(transferId),
                new AccountId(sourceAccountId),
                new AccountId(targetAccountId),
                new Money(new BigDecimal("100.00"), Currency.PEN),
                TransferType.INTERNAL,
                new IdempotencyKey("key-123"),
                TransferStatus.PENDING,
                java.time.Instant.now(),
                java.time.Instant.now()
        );

        AccountSnapshot sourceSnapshot = new AccountSnapshot(
                new AccountId(sourceAccountId),
                new CustomerId(sourceCustomerId),
                Currency.PEN,
                new BigDecimal("900.00"),
                true
        );

        when(transferRepositoryPort.findById(new TransferId(transferId)))
                .thenReturn(Optional.of(transfer));

        when(transferRepositoryPort.save(any(Transfer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(accountSnapshotRepositoryPort.findByAccountId(new AccountId(sourceAccountId)))
                .thenReturn(Optional.of(sourceSnapshot));

        service.handleTransferFailed(transferId);

        verify(transferRepositoryPort).save(any(Transfer.class));
        verify(outboxEventRepositoryPort).save(any(OutboxEvent.class));
        verify(accountSnapshotRepositoryPort).findByAccountId(new AccountId(sourceAccountId));
    }
}