package com.banking.audit.domain.model;

import java.util.Objects;
import java.util.UUID;

public record AuditEventId(UUID value) {

    public AuditEventId {
        Objects.requireNonNull(value, "AuditEventId value cannot be null");
    }

    public static AuditEventId of(UUID value) {
        return new AuditEventId(value);
    }
}