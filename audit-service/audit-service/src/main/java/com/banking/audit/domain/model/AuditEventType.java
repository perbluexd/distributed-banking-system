package com.banking.audit.domain.model;

public enum AuditEventType {

    USER_REGISTERED,
    USER_LOGGED_IN,

    CUSTOMER_CREATED,

    ACCOUNT_CREATED,
    ACCOUNT_ACTIVATED,
    ACCOUNT_BLOCKED,
    ACCOUNT_DEBITED,
    ACCOUNT_CREDITED,

    TRANSFER_CREATED,
    TRANSFER_COMPLETED,
    TRANSFER_FAILED
}