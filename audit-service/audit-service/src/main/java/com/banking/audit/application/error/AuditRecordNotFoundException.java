package com.banking.audit.application.error;

public class AuditRecordNotFoundException extends RuntimeException {

    public AuditRecordNotFoundException(String message) {
        super(message);
    }
}