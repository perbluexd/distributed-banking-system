CREATE TABLE transfers (
                           id UUID PRIMARY KEY,
                           source_account_id UUID NOT NULL,
                           target_account_id UUID NOT NULL,
                           amount NUMERIC(19, 2) NOT NULL,
                           currency VARCHAR(20) NOT NULL,
                           type VARCHAR(30) NOT NULL,
                           idempotency_key VARCHAR(150) NOT NULL UNIQUE,
                           status VARCHAR(30) NOT NULL,
                           created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                           updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_transfers_source_account_id
    ON transfers(source_account_id);

CREATE INDEX idx_transfers_target_account_id
    ON transfers(target_account_id);

CREATE TABLE account_snapshots (
                                   account_id UUID PRIMARY KEY,
                                   customer_id UUID NOT NULL,
                                   currency VARCHAR(20) NOT NULL,
                                   balance NUMERIC(19, 2) NOT NULL,
                                   active BOOLEAN NOT NULL,
                                   updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE idempotency_keys (
                                  idempotency_key VARCHAR(150) PRIMARY KEY,
                                  transfer_id UUID NOT NULL,
                                  created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_idempotency_keys_transfer_id
    ON idempotency_keys(transfer_id);

CREATE TABLE outbox_events (
                               id UUID PRIMARY KEY,
                               aggregate_type VARCHAR(100) NOT NULL,
                               aggregate_id UUID NOT NULL,
                               event_type VARCHAR(150) NOT NULL,
                               topic VARCHAR(150) NOT NULL,
                               event_key VARCHAR(150) NOT NULL,
                               payload TEXT NOT NULL,
                               status VARCHAR(30) NOT NULL,
                               created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                               published_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_outbox_events_status_created_at
    ON outbox_events(status, created_at);