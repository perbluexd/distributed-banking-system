package com.banking.transaction.api.controller;

import com.banking.transaction.api.dto.CreateTransferRequest;
import com.banking.transaction.api.dto.TransferResponse;
import com.banking.transaction.api.mapper.TransferApiMapper;
import com.banking.transaction.application.command.CreateTransferCommand;
import com.banking.transaction.application.error.TransferNotFoundException;
import com.banking.transaction.application.port.in.CreateTransferUseCase;
import com.banking.transaction.application.port.in.GetTransferByIdUseCase;
import com.banking.transaction.application.port.in.GetTransfersByAccountIdUseCase;
import com.banking.transaction.domain.model.Transfer;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final CreateTransferUseCase createTransferUseCase;
    private final GetTransferByIdUseCase getTransferByIdUseCase;
    private final GetTransfersByAccountIdUseCase getTransfersByAccountIdUseCase;

    public TransferController(
            CreateTransferUseCase createTransferUseCase,
            GetTransferByIdUseCase getTransferByIdUseCase,
            GetTransfersByAccountIdUseCase getTransfersByAccountIdUseCase
    ) {
        this.createTransferUseCase = createTransferUseCase;
        this.getTransferByIdUseCase = getTransferByIdUseCase;
        this.getTransfersByAccountIdUseCase = getTransfersByAccountIdUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UUID createTransfer(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreateTransferRequest request
    ) {
        CreateTransferCommand command = new CreateTransferCommand(
                request.sourceAccountId(),
                request.targetAccountId(),
                request.amount(),
                request.currency(),
                idempotencyKey
        );

        return createTransferUseCase.createTransfer(command);
    }

    @GetMapping("/{transferId}")
    public TransferResponse getTransferById(@PathVariable UUID transferId) {
        Transfer transfer = getTransferByIdUseCase.getById(transferId)
                .orElseThrow(() -> new TransferNotFoundException(transferId));

        return TransferApiMapper.toResponse(transfer);
    }

    @GetMapping("/accounts/{accountId}")
    public List<TransferResponse> getTransfersByAccountId(@PathVariable UUID accountId) {
        return getTransfersByAccountIdUseCase.getByAccountId(accountId)
                .stream()
                .map(TransferApiMapper::toResponse)
                .toList();
    }
}