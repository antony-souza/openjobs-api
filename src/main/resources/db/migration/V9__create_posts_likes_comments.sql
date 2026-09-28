CREATE TABLE posts (
    id UUID PRIMARY KEY,
    context VARCHAR(1000) NOT NULL,
    file_url VARCHAR(255),
    user_id UUID NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6),
    deleted_at TIMESTAMP(6),
    CONSTRAINT fk_posts_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
);

CREATE INDEX idx_posts_user_id ON posts (user_id);

CREATE TABLE likes (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    post_id UUID NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6),
    deleted_at TIMESTAMP(6),
    CONSTRAINT fk_likes_user
        FOREIGN KEY (user_id)
        REFERENCES users (id),
    CONSTRAINT fk_likes_post
        FOREIGN KEY (post_id)
        REFERENCES posts (id),
    CONSTRAINT uk_likes_user_post UNIQUE (user_id, post_id)
);

CREATE INDEX idx_likes_post_id ON likes (post_id);

CREATE TABLE comments (
    id UUID PRIMARY KEY,
    content VARCHAR(1000) NOT NULL,
    user_id UUID NOT NULL,
    post_id UUID NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6),
    deleted_at TIMESTAMP(6),
    CONSTRAINT fk_comments_user
        FOREIGN KEY (user_id)
        REFERENCES users (id),
    CONSTRAINT fk_comments_post
        FOREIGN KEY (post_id)
        REFERENCES posts (id)
);

CREATE INDEX idx_comments_user_id ON comments (user_id);
CREATE INDEX idx_comments_post_id ON comments (post_id);
