CREATE TABLE customers (
                           id UUID PRIMARY KEY,
                           user_id UUID NOT NULL UNIQUE,
                           dni VARCHAR(20) UNIQUE,
                           full_name VARCHAR(150),
                           email VARCHAR(150) NOT NULL UNIQUE,
                           status VARCHAR(30) NOT NULL,
                           created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_customers_user_id ON customers(user_id);
CREATE INDEX idx_customers_email ON customers(email);