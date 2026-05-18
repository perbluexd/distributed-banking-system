package com.banking.transaction.infrastructure.client.exception;

public class AccountCommandFailedException extends AccountServiceException {

    public AccountCommandFailedException(String message) {
        super(message);
    }

    public AccountCommandFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}