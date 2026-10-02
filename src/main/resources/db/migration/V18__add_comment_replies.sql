ALTER TABLE comments
    ADD COLUMN parent_comment_id UUID,
    ADD CONSTRAINT fk_comments_parent
        FOREIGN KEY (parent_comment_id) REFERENCES comments (id),
    ADD CONSTRAINT chk_comments_not_own_parent
        CHECK (parent_comment_id IS NULL OR parent_comment_id <> id);

CREATE INDEX idx_comments_parent_active
    ON comments (parent_comment_id, created_at, id)
    WHERE deleted_at IS NULL;
