package com.banking.audit.domain.model;

import java.util.Objects;
import java.util.UUID;

public record AuditAggregateId(UUID value) {

    public AuditAggregateId {
        Objects.requireNonNull(value, "AuditAggregateId value cannot be null");
    }

    public static AuditAggregateId of(UUID value) {
        return new AuditAggregateId(value);
    }
}