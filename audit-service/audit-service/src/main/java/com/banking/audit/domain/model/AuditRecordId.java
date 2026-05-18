package com.banking.audit.domain.model;

import java.util.Objects;
import java.util.UUID;

public record AuditRecordId(UUID value) {

    public AuditRecordId {
        Objects.requireNonNull(value, "AuditRecordId value cannot be null");
    }

    public static AuditRecordId newId() {
        return new AuditRecordId(UUID.randomUUID());
    }

    public static AuditRecordId of(UUID value) {
        return new AuditRecordId(value);
    }
}