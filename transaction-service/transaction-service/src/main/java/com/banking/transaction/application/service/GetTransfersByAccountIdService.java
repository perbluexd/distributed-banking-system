package com.banking.transaction.application.service;

import com.banking.transaction.application.port.in.GetTransfersByAccountIdUseCase;
import com.banking.transaction.application.port.out.TransferRepositoryPort;
import com.banking.transaction.domain.model.AccountId;
import com.banking.transaction.domain.model.Transfer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetTransfersByAccountIdService implements GetTransfersByAccountIdUseCase {

    private final TransferRepositoryPort transferRepositoryPort;

    public GetTransfersByAccountIdService(TransferRepositoryPort transferRepositoryPort) {
        this.transferRepositoryPort = transferRepositoryPort;
    }

    @Override
    public List<Transfer> getByAccountId(UUID accountId) {
        return transferRepositoryPort.findByAccountId(new AccountId(accountId));
    }
}