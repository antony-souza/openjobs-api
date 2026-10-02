package com.antony.openjobs.modules.menuitens.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.antony.openjobs.common.repositories.IBaseRepository;
import com.antony.openjobs.modules.menuitens.model.MenuItemEntity;

@Repository 
public interface IMenuItemRepository extends IBaseRepository<MenuItemEntity, UUID> {
    List<MenuItemEntity> findAllByDeletedAtIsNull();

    boolean existsByPath(String path);

    Optional<MenuItemEntity> findByPath(String path);
}
