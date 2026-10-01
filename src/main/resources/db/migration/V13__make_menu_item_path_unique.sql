ALTER TABLE menu_items
    ADD CONSTRAINT uk_menu_items_path UNIQUE (path);
