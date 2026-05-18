package com.banking.transaction.application.service;

import com.banking.transaction.application.command.CreateTransferCommand;
import com.banking.transaction.application.error.AccountSnapshotNotFoundException;
import com.banking.transaction.application.port.out.*;
import com.banking.transaction.domain.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateTransferServiceTest {

    private final TransferRepositoryPort transferRepositoryPort = mock(TransferRepositoryPort.class);
    private final AccountSnapshotRepositoryPort accountSnapshotRepositoryPort = mock(AccountSnapshotRepositoryPort.class);
    private final IdempotencyRepositoryPort idempotencyRepositoryPort = mock(IdempotencyRepositoryPort.class);
    private final OutboxEventRepositoryPort outboxEventRepositoryPort = mock(OutboxEventRepositoryPort.class);
    private final AccountCommandPort accountCommandPort = mock(AccountCommandPort.class);
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private final CreateTransferService service = new CreateTransferService(
            transferRepositoryPort,
            accountSnapshotRepositoryPort,
            idempotencyRepositoryPort,
            outboxEventRepositoryPort,
            accountCommandPort,
            objectMapper
    );

    @Test
    void shouldReturnExistingTransferIdWhenIdempotencyKeyAlreadyExists() {
        UUID existingTransferId = UUID.randomUUID();

        CreateTransferCommand command = new CreateTransferCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("100.00"),
                Currency.PEN,
                "key-123"
        );

        when(idempotencyRepositoryPort.findTransferIdByKey(new IdempotencyKey("key-123")))
                .thenReturn(Optional.of(existingTransferId));

        UUID result = service.createTransfer(command);

        assertEquals(existingTransferId, result);
        verifyNoInteractions(transferRepositoryPort);
        verifyNoInteractions(accountSnapshotRepositoryPort);
        verifyNoInteractions(outboxEventRepositoryPort);
    }

    @Test
    void shouldCreateTransferAndSaveOutboxEvent() {
        UUID sourceAccountId = UUID.randomUUID();
        UUID targetAccountId = UUID.randomUUID();

        CreateTransferCommand command = new CreateTransferCommand(
                sourceAccountId,
                targetAccountId,
                new BigDecimal("100.00"),
                Currency.PEN,
                "key-123"
        );

        AccountSnapshot sourceSnapshot = new AccountSnapshot(
                new AccountId(sourceAccountId),
                UUID.randomUUID() == null ? null : new CustomerId(UUID.randomUUID()),
                Currency.PEN,
                new BigDecimal("500.00"),
                true
        );

        AccountSnapshot targetSnapshot = new AccountSnapshot(
                new AccountId(targetAccountId),
                new CustomerId(UUID.randomUUID()),
                Currency.PEN,
                new BigDecimal("200.00"),
                true
        );

        when(idempotencyRepositoryPort.findTransferIdByKey(new IdempotencyKey("key-123")))
                .thenReturn(Optional.empty());

        when(accountSnapshotRepositoryPort.findByAccountId(new AccountId(sourceAccountId)))
                .thenReturn(Optional.of(sourceSnapshot));

        when(accountSnapshotRepositoryPort.findByAccountId(new AccountId(targetAccountId)))
                .thenReturn(Optional.of(targetSnapshot));

        when(transferRepositoryPort.save(any(Transfer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(outboxEventRepositoryPort.save(any(OutboxEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UUID result = service.createTransfer(command);

        assertNotNull(result);

        verify(transferRepositoryPort).save(any(Transfer.class));
        verify(idempotencyRepositoryPort).save(eq(new IdempotencyKey("key-123")), eq(result));
        verify(outboxEventRepositoryPort).save(any(OutboxEvent.class));
    }

    @Test
    void shouldThrowWhenSourceSnapshotDoesNotExist() {
        UUID sourceAccountId = UUID.randomUUID();
        UUID targetAccountId = UUID.randomUUID();

        CreateTransferCommand command = new CreateTransferCommand(
                sourceAccountId,
                targetAccountId,
                new BigDecimal("100.00"),
                Currency.PEN,
                "key-123"
        );

        when(idempotencyRepositoryPort.findTransferIdByKey(new IdempotencyKey("key-123")))
                .thenReturn(Optional.empty());

        when(accountSnapshotRepositoryPort.findByAccountId(new AccountId(sourceAccountId)))
                .thenReturn(Optional.empty());

        assertThrows(AccountSnapshotNotFoundException.class,
                () -> service.createTransfer(command));
    }
}