CREATE TABLE users (
    id  BIGSERIAL PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password_hash VARCHAR(256) NOT NULL,
    display_name VARCHAR(128),
    bio TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);