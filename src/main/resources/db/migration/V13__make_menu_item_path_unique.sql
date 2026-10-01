ALTER TABLE menu_items
    ALTER COLUMN path TYPE VARCHAR(1010);

ALTER TABLE menu_items
    ADD CONSTRAINT uk_menu_items_path UNIQUE (path);
