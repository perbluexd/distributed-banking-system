package com.banking.transaction.application.port.in;

import com.banking.transaction.application.command.CreateTransferCommand;

import java.util.UUID;

public interface CreateTransferUseCase {

    UUID createTransfer(CreateTransferCommand command);
}