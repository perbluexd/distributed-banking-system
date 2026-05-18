package com.banking.transaction.infrastructure.client.exception;

public class AccountServiceUnavailableException extends AccountServiceException {

    public AccountServiceUnavailableException(String message) {
        super(message);
    }

    public AccountServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}