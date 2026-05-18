package com.banking.audit.api.mapper;

import com.banking.audit.api.dto.AuditRecordResponse;
import com.banking.audit.api.dto.AuditRecordSearchRequest;
import com.banking.audit.application.query.SearchAuditRecordsQuery;
import com.banking.audit.domain.model.*;
import org.springframework.stereotype.Component;

@Component
public class AuditApiMapper {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public AuditRecordResponse toResponse(AuditRecord auditRecord) {
        return new AuditRecordResponse(
                auditRecord.getId().value(),
                auditRecord.getEventId().value(),
                auditRecord.getEventType().name(),
                auditRecord.getAggregateType().name(),
                auditRecord.getAggregateId().value(),
                auditRecord.getSourceService().name(),
                auditRecord.getSeverity().name(),
                auditRecord.getActor().actorId(),
                auditRecord.getActor().actorType(),
                auditRecord.getAction().name(),
                auditRecord.getOutcome().name(),
                auditRecord.getPayloadJson(),
                auditRecord.getOccurredAt(),
                auditRecord.getRecordedAt()
        );
    }

    public SearchAuditRecordsQuery toQuery(AuditRecordSearchRequest request) {
        int page = request.page() == null ? DEFAULT_PAGE : request.page();
        int size = request.size() == null ? DEFAULT_SIZE : request.size();

        return new SearchAuditRecordsQuery(
                parseEnum(request.eventType(), AuditEventType.class),
                parseEnum(request.aggregateType(), AuditAggregateType.class),
                request.aggregateId() == null ? null : AuditAggregateId.of(request.aggregateId()),
                parseEnum(request.sourceService(), AuditSourceService.class),
                parseEnum(request.severity(), AuditSeverity.class),
                parseEnum(request.outcome(), AuditOutcome.class),
                request.occurredFrom(),
                request.occurredTo(),
                page,
                size
        );
    }

    private <E extends Enum<E>> E parseEnum(String value, Class<E> enumType) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Enum.valueOf(enumType, value.trim().toUpperCase());
    }
}