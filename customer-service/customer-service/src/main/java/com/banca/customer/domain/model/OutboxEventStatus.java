package com.banca.customer.domain.model;

public enum OutboxEventStatus {
    PENDING,
    PUBLISHED,
    FAILED
}