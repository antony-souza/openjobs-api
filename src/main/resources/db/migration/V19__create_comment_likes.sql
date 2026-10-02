CREATE TABLE comment_likes (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users (id),
    comment_id UUID NOT NULL REFERENCES comments (id),
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6),
    deleted_at TIMESTAMP(6),
    CONSTRAINT uk_comment_likes_user_comment UNIQUE (user_id, comment_id)
);

CREATE INDEX idx_comment_likes_comment_active
    ON comment_likes (comment_id, created_at, id)
    WHERE deleted_at IS NULL;
