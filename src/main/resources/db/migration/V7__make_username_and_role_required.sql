ALTER TABLE users
    ALTER COLUMN username SET NOT NULL,
    ADD CONSTRAINT uk_users_username UNIQUE (username);

ALTER TABLE users
    DROP CONSTRAINT uk_users_role_id,
    ALTER COLUMN role_id SET NOT NULL;
