CREATE INDEX idx_posts_feed ON posts (created_at DESC, id DESC) WHERE deleted_at IS NULL;
CREATE INDEX idx_comments_post_active ON comments (post_id, created_at DESC, id DESC) WHERE deleted_at IS NULL;
