package com.banking.transaction.application.port.in;

import com.banking.transaction.domain.model.Transfer;

import java.util.List;
import java.util.UUID;

public interface GetTransfersByAccountIdUseCase {

    List<Transfer> getByAccountId(UUID accountId);
}