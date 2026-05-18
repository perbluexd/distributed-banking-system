package com.banking.auth.domain.model;

public enum OutboxEventStatus {
    PENDING,
    PUBLISHED,
    FAILED
}