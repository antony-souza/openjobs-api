ALTER TABLE users
    ADD COLUMN role_id UUID;

ALTER TABLE users
    ADD CONSTRAINT fk_users_role
        FOREIGN KEY (role_id)
        REFERENCES roles (id);

ALTER TABLE users
    ADD CONSTRAINT uk_users_role_id UNIQUE (role_id);
