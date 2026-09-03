CREATE TABLE applications (
    id UUID PRIMARY KEY,
    candidate_id UUID NOT NULL,
    job_id UUID NOT NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6),
    deleted_at TIMESTAMP(6),
    CONSTRAINT fk_applications_candidate
        FOREIGN KEY (candidate_id)
        REFERENCES users (id),
    CONSTRAINT fk_applications_job
        FOREIGN KEY (job_id)
        REFERENCES jobs (id)
);

CREATE INDEX idx_applications_candidate_id ON applications (candidate_id);
CREATE INDEX idx_applications_job_id ON applications (job_id);
