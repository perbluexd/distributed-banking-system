package com.banking.transaction.api.controller;

import com.banking.transaction.application.port.in.CreateTransferUseCase;
import com.banking.transaction.application.port.in.GetTransferByIdUseCase;
import com.banking.transaction.application.port.in.GetTransfersByAccountIdUseCase;
import com.banking.transaction.domain.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TransferControllerTest {

    private final CreateTransferUseCase createTransferUseCase = mock(CreateTransferUseCase.class);
    private final GetTransferByIdUseCase getTransferByIdUseCase = mock(GetTransferByIdUseCase.class);
    private final GetTransfersByAccountIdUseCase getTransfersByAccountIdUseCase = mock(GetTransfersByAccountIdUseCase.class);

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new TransferController(
                    createTransferUseCase,
                    getTransferByIdUseCase,
                    getTransfersByAccountIdUseCase
            ))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateTransfer() throws Exception {
        UUID transferId = UUID.randomUUID();

        when(createTransferUseCase.createTransfer(any()))
                .thenReturn(transferId);

        String request = """
                {
                  "sourceAccountId": "%s",
                  "targetAccountId": "%s",
                  "amount": 100.00,
                  "currency": "PEN"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(post("/transfers")
                        .header("Idempotency-Key", "key-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(content().string("\"" + transferId + "\""));

        verify(createTransferUseCase).createTransfer(any());
    }

    @Test
    void shouldGetTransferById() throws Exception {
        Transfer transfer = createTransfer();

        when(getTransferByIdUseCase.getById(transfer.getId().value()))
                .thenReturn(Optional.of(transfer));

        mockMvc.perform(get("/transfers/{transferId}", transfer.getId().value()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transferId").value(transfer.getId().value().toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldGetTransfersByAccountId() throws Exception {
        Transfer transfer = createTransfer();
        UUID accountId = transfer.getSourceAccountId().value();

        when(getTransfersByAccountIdUseCase.getByAccountId(accountId))
                .thenReturn(List.of(transfer));

        mockMvc.perform(get("/transfers/accounts/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transferId").value(transfer.getId().value().toString()));
    }

    private Transfer createTransfer() {
        return Transfer.create(
                new AccountId(UUID.randomUUID()),
                new AccountId(UUID.randomUUID()),
                new Money(new BigDecimal("100.00"), Currency.PEN),
                TransferType.INTERNAL,
                new IdempotencyKey("key-123")
        );
    }
}