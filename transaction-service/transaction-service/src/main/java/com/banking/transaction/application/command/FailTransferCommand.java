package com.banking.transaction.application.command;

public record FailTransferCommand(
        String reason,
        String idempotencyKey
) {
}