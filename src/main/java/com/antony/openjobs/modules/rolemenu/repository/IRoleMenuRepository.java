package com.antony.openjobs.modules.rolemenu.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.antony.openjobs.common.repositories.IBaseRepository;
import com.antony.openjobs.modules.rolemenu.model.RoleMenuEntity;

@Repository
public interface IRoleMenuRepository extends IBaseRepository<RoleMenuEntity, UUID> {
    Optional<RoleMenuEntity> findByRole_IdAndMenuItem_Id(UUID roleId, UUID menuItemId);
}
