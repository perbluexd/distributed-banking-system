CREATE TABLE accounts (
                          id UUID PRIMARY KEY,
                          customer_id UUID NOT NULL,
                          account_number VARCHAR(30) NOT NULL,
                          account_type VARCHAR(30) NOT NULL,
                          status VARCHAR(30) NOT NULL,
                          balance NUMERIC(19, 2) NOT NULL,
                          currency VARCHAR(10) NOT NULL,
                          version BIGINT NOT NULL DEFAULT 0,
                          created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                          updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

                          CONSTRAINT uk_accounts_account_number UNIQUE (account_number),
                          CONSTRAINT chk_accounts_balance_non_negative CHECK (balance >= 0),
                          CONSTRAINT chk_accounts_account_type CHECK (account_type IN ('SAVINGS', 'CHECKING')),
                          CONSTRAINT chk_accounts_status CHECK (status IN ('ACTIVE', 'BLOCKED', 'CLOSED')),
                          CONSTRAINT chk_accounts_currency CHECK (currency IN ('PEN', 'USD', 'EUR'))
);

CREATE INDEX idx_accounts_customer_id ON accounts(customer_id);