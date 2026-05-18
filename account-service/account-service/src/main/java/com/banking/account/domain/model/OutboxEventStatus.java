package com.banking.account.domain.model;

public enum OutboxEventStatus {
    PENDING,
    PUBLISHED,
    FAILED
}