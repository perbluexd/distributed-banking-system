package com.banking.audit.domain.model;

public enum AuditAction {

    REGISTERED,
    LOGGED_IN,

    CREATED,
    ACTIVATED,
    BLOCKED,

    DEBITED,
    CREDITED,

    TRANSFER_CREATED,
    TRANSFER_COMPLETED,
    TRANSFER_FAILED
}