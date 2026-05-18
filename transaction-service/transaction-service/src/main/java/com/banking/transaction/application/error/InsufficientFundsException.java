package com.banking.transaction.application.error;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException() {
        super("Insufficient funds for this transfer");
    }

    public InsufficientFundsException(String message) {
        super(message);
    }
}