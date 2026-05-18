package com.banking.audit.infrastructure.persistence.adapter;

import com.banking.audit.application.port.out.AuditRecordRepositoryPort;
import com.banking.audit.application.query.SearchAuditRecordsQuery;
import com.banking.audit.domain.model.AuditEventId;
import com.banking.audit.domain.model.AuditRecord;
import com.banking.audit.domain.model.AuditRecordId;
import com.banking.audit.infrastructure.persistence.entity.AuditRecordJpaEntity;
import com.banking.audit.infrastructure.persistence.mapper.AuditRecordPersistenceMapper;
import com.banking.audit.infrastructure.persistence.repository.AuditRecordJpaRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AuditRecordPersistenceAdapter implements AuditRecordRepositoryPort {

    private final AuditRecordJpaRepository auditRecordJpaRepository;
    private final AuditRecordPersistenceMapper auditRecordPersistenceMapper;

    public AuditRecordPersistenceAdapter(
            AuditRecordJpaRepository auditRecordJpaRepository,
            AuditRecordPersistenceMapper auditRecordPersistenceMapper
    ) {
        this.auditRecordJpaRepository = auditRecordJpaRepository;
        this.auditRecordPersistenceMapper = auditRecordPersistenceMapper;
    }

    @Override
    public AuditRecord save(AuditRecord auditRecord) {
        AuditRecordJpaEntity entity = auditRecordPersistenceMapper.toEntity(auditRecord);
        AuditRecordJpaEntity savedEntity = auditRecordJpaRepository.save(entity);

        return auditRecordPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<AuditRecord> findById(AuditRecordId auditRecordId) {
        return auditRecordJpaRepository.findById(auditRecordId.value())
                .map(auditRecordPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEventId(AuditEventId eventId) {
        return auditRecordJpaRepository.existsByEventId(eventId.value());
    }

    @Override
    public List<AuditRecord> search(SearchAuditRecordsQuery query) {
        Specification<AuditRecordJpaEntity> specification = buildSpecification(query);

        PageRequest pageRequest = PageRequest.of(
                query.page(),
                query.size(),
                Sort.by(Sort.Direction.DESC, "occurredAt")
        );

        return auditRecordJpaRepository.findAll(specification, pageRequest)
                .stream()
                .map(auditRecordPersistenceMapper::toDomain)
                .toList();
    }

    private Specification<AuditRecordJpaEntity> buildSpecification(SearchAuditRecordsQuery query) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query.eventType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("eventType"), query.eventType()));
            }

            if (query.aggregateType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("aggregateType"), query.aggregateType()));
            }

            if (query.aggregateId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("aggregateId"), query.aggregateId().value()));
            }

            if (query.sourceService() != null) {
                predicates.add(criteriaBuilder.equal(root.get("sourceService"), query.sourceService()));
            }

            if (query.severity() != null) {
                predicates.add(criteriaBuilder.equal(root.get("severity"), query.severity()));
            }

            if (query.outcome() != null) {
                predicates.add(criteriaBuilder.equal(root.get("outcome"), query.outcome()));
            }

            if (query.occurredFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("occurredAt"),
                        query.occurredFrom()
                ));
            }

            if (query.occurredTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("occurredAt"),
                        query.occurredTo()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}