package com.banking.transaction.application.service;

import com.banking.transaction.application.port.in.GetTransferByIdUseCase;
import com.banking.transaction.application.port.out.TransferRepositoryPort;
import com.banking.transaction.domain.model.Transfer;
import com.banking.transaction.domain.model.TransferId;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GetTransferByIdService implements GetTransferByIdUseCase {

    private final TransferRepositoryPort transferRepositoryPort;

    public GetTransferByIdService(TransferRepositoryPort transferRepositoryPort) {
        this.transferRepositoryPort = transferRepositoryPort;
    }

    @Override
    public Optional<Transfer> getById(UUID transferId) {
        return transferRepositoryPort.findById(new TransferId(transferId));
    }
}