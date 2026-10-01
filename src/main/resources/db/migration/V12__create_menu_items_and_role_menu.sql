CREATE TABLE menu_items (
    id UUID PRIMARY KEY,
    title VARCHAR(50) NOT NULL,
    icon_name VARCHAR(50) NOT NULL,
    path VARCHAR(100) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6),
    deleted_at TIMESTAMP(6)
);

CREATE TABLE role_menu (
    id UUID PRIMARY KEY,
    role_id UUID NOT NULL,
    menu_item_id UUID NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6),
    deleted_at TIMESTAMP(6),
    CONSTRAINT fk_role_menu_role
        FOREIGN KEY (role_id)
        REFERENCES roles (id),
    CONSTRAINT fk_role_menu_menu_item
        FOREIGN KEY (menu_item_id)
        REFERENCES menu_items (id),
    CONSTRAINT uk_role_menu_role_menu_item
        UNIQUE (role_id, menu_item_id)
);

CREATE INDEX idx_role_menu_role_id ON role_menu (role_id);
CREATE INDEX idx_role_menu_menu_item_id ON role_menu (menu_item_id);
