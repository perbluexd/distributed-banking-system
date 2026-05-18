package com.banking.audit.infrastructure.persistence.mapper;

import com.banking.audit.domain.model.AuditActor;
import com.banking.audit.domain.model.AuditAggregateId;
import com.banking.audit.domain.model.AuditEventId;
import com.banking.audit.domain.model.AuditRecord;
import com.banking.audit.domain.model.AuditRecordId;
import com.banking.audit.infrastructure.persistence.entity.AuditRecordJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AuditRecordPersistenceMapper {

    public AuditRecordJpaEntity toEntity(AuditRecord auditRecord) {
        return new AuditRecordJpaEntity(
                auditRecord.getId().value(),
                auditRecord.getEventId().value(),
                auditRecord.getEventType(),
                auditRecord.getAggregateType(),
                auditRecord.getAggregateId().value(),
                auditRecord.getSourceService(),
                auditRecord.getSeverity(),
                auditRecord.getActor().actorId(),
                auditRecord.getActor().actorType(),
                auditRecord.getAction(),
                auditRecord.getOutcome(),
                auditRecord.getPayloadJson(),
                auditRecord.getOccurredAt(),
                auditRecord.getRecordedAt()
        );
    }

    public AuditRecord toDomain(AuditRecordJpaEntity entity) {
        return AuditRecord.restore(
                AuditRecordId.of(entity.getId()),
                AuditEventId.of(entity.getEventId()),
                entity.getEventType(),
                entity.getAggregateType(),
                AuditAggregateId.of(entity.getAggregateId()),
                entity.getSourceService(),
                entity.getSeverity(),
                new AuditActor(entity.getActorId(), entity.getActorType()),
                entity.getAction(),
                entity.getOutcome(),
                entity.getPayloadJson(),
                entity.getOccurredAt(),
                entity.getRecordedAt()
        );
    }
}