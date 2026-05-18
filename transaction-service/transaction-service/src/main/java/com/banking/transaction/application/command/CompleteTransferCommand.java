package com.banking.transaction.application.command;

import java.util.UUID;

public record CompleteTransferCommand(
        UUID transferId
) {
}