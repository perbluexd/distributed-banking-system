package com.banking.audit.application.error;

public class InvalidAuditRecordException extends RuntimeException {

    public InvalidAuditRecordException(String message) {
        super(message);
    }
}