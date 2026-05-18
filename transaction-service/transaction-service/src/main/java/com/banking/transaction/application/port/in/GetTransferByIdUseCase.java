package com.banking.transaction.application.port.in;

import com.banking.transaction.domain.model.Transfer;

import java.util.Optional;
import java.util.UUID;

public interface GetTransferByIdUseCase {

    Optional<Transfer> getById(UUID transferId);
}