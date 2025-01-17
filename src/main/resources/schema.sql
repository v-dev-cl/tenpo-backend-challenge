DROP TABLE IF EXISTS endpoint_history;

CREATE TABLE IF NOT EXISTS endpoint_history (
    id SERIAL PRIMARY KEY,
    ip VARCHAR(255),
    endpoint VARCHAR(255),
    parameters VARCHAR(255),
    response VARCHAR(255),
    error VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);