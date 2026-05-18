CREATE TABLE notifications (
                               id UUID PRIMARY KEY,
                               transfer_id UUID NOT NULL,
                               type VARCHAR(50) NOT NULL,
                               channel VARCHAR(50) NOT NULL,
                               recipient_email VARCHAR(255) NOT NULL,
                               status VARCHAR(50) NOT NULL,
                               attempt_count INTEGER NOT NULL,
                               error_message TEXT,
                               created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                               sent_at TIMESTAMP WITH TIME ZONE,

                               CONSTRAINT uk_notifications_transfer_type UNIQUE (transfer_id, type)
);

CREATE INDEX idx_notifications_transfer_id
    ON notifications (transfer_id);

CREATE INDEX idx_notifications_status
    ON notifications (status);


CREATE TABLE customer_snapshots (
                                    customer_id UUID PRIMARY KEY,
                                    user_id UUID NOT NULL,
                                    email VARCHAR(255) NOT NULL,
                                    status VARCHAR(50) NOT NULL,
                                    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_customer_snapshots_user_id
    ON customer_snapshots (user_id);

CREATE INDEX idx_customer_snapshots_email
    ON customer_snapshots (email);