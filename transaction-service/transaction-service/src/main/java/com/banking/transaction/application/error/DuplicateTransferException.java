package com.banking.transaction.application.error;

public class DuplicateTransferException extends RuntimeException {

    public DuplicateTransferException(String idempotencyKey) {
        super("Duplicate transfer detected for idempotency key: " + idempotencyKey);
    }
}