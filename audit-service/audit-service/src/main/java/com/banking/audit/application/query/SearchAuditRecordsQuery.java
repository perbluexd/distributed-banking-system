package com.banking.audit.application.query;

import com.banking.audit.domain.model.AuditAggregateId;
import com.banking.audit.domain.model.AuditAggregateType;
import com.banking.audit.domain.model.AuditEventType;
import com.banking.audit.domain.model.AuditOutcome;
import com.banking.audit.domain.model.AuditSeverity;
import com.banking.audit.domain.model.AuditSourceService;

import java.time.Instant;

public record SearchAuditRecordsQuery(
        AuditEventType eventType,
        AuditAggregateType aggregateType,
        AuditAggregateId aggregateId,
        AuditSourceService sourceService,
        AuditSeverity severity,
        AuditOutcome outcome,
        Instant occurredFrom,
        Instant occurredTo,
        int page,
        int size
) {

    private static final int MAX_PAGE_SIZE = 100;

    public SearchAuditRecordsQuery {
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }

        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than zero");
        }

        if (size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Size cannot be greater than " + MAX_PAGE_SIZE);
        }

        if (occurredFrom != null && occurredTo != null && occurredFrom.isAfter(occurredTo)) {
            throw new IllegalArgumentException("Occurred from cannot be after occurred to");
        }
    }
}