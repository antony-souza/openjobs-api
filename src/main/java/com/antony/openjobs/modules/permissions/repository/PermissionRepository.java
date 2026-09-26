package com.antony.openjobs.modules.permissions.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.antony.openjobs.common.repositories.IBaseRepository;
import com.antony.openjobs.modules.permissions.model.PermissionEntity;

@Repository
public interface PermissionRepository extends IBaseRepository<PermissionEntity, UUID> {
    boolean existsByCodeAndDeletedAtIsNull(String code);
}
