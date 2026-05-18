package com.banking.transaction.application.port.in;

import java.util.UUID;

public interface RetryTransferUseCase {

    void retry(UUID transferId);
}