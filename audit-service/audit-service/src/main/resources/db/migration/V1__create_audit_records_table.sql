CREATE TABLE audit_records (
                               id UUID PRIMARY KEY,
                               event_id UUID NOT NULL UNIQUE,

                               event_type VARCHAR(80) NOT NULL,
                               aggregate_type VARCHAR(80) NOT NULL,
                               aggregate_id UUID NOT NULL,

                               source_service VARCHAR(80) NOT NULL,
                               severity VARCHAR(40) NOT NULL,

                               actor_id UUID,
                               actor_type VARCHAR(80) NOT NULL,

                               action VARCHAR(80) NOT NULL,
                               outcome VARCHAR(40) NOT NULL,

                               payload_json TEXT NOT NULL,

                               occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
                               recorded_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_audit_records_event_type
    ON audit_records(event_type);

CREATE INDEX idx_audit_records_aggregate
    ON audit_records(aggregate_type, aggregate_id);

CREATE INDEX idx_audit_records_source_service
    ON audit_records(source_service);

CREATE INDEX idx_audit_records_severity
    ON audit_records(severity);

CREATE INDEX idx_audit_records_outcome
    ON audit_records(outcome);

CREATE INDEX idx_audit_records_occurred_at
    ON audit_records(occurred_at);

CREATE INDEX idx_audit_records_recorded_at
    ON audit_records(recorded_at);