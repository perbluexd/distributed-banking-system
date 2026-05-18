package com.banking.audit.application.error;

public class DuplicateAuditEventException extends RuntimeException {

    public DuplicateAuditEventException(String message) {
        super(message);
    }
}