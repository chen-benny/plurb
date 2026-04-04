CREATE TABLE series (
    id BIGSERIAL PRIMARY KEY,
    author_id BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(256) NOT NULL,
    slug VARCHAR(256) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (author_id, slug)
);

CREATE TABLE post_series (
    post_id BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    series_id BIGINT NOT NULL REFERENCES series(id) ON DELETE CASCADE,
    position INT NOT NULL,
    PRIMARY KEY (post_id, series_id)
);