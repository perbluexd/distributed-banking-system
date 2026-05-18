CREATE TABLE customer_snapshots (
                                    customer_id UUID PRIMARY KEY,
                                    user_id UUID NOT NULL UNIQUE,
                                    email VARCHAR(150) NOT NULL,
                                    status VARCHAR(30) NOT NULL,
                                    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                    last_event_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_customer_snapshots_user_id ON customer_snapshots(user_id);
CREATE INDEX idx_customer_snapshots_email ON customer_snapshots(email);
CREATE INDEX idx_customer_snapshots_status ON customer_snapshots(status);