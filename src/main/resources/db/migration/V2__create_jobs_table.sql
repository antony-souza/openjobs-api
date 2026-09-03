CREATE TABLE jobs (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    published_by UUID NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6),
    deleted_at TIMESTAMP(6),
    CONSTRAINT fk_jobs_published_by
        FOREIGN KEY (published_by)
        REFERENCES users (id)
);

CREATE INDEX idx_jobs_published_by ON jobs (published_by);
