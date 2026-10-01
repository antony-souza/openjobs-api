package com.antony.openjobs.modules.permissions.repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.antony.openjobs.common.repositories.IBaseRepository;
import com.antony.openjobs.modules.permissions.model.PermissionEntity;

@Repository
public interface IPermissionRepository extends IBaseRepository<PermissionEntity, UUID> {
    List<PermissionEntity> findAllByDeletedAtIsNull();

    Optional<PermissionEntity> findByCode(String code);

    boolean existsByCodeAndDeletedAtIsNull(String code);

    Optional<PermissionEntity> findByCodeAndDeletedAtIsNull(String code);

    Optional<PermissionEntity> findByIdAndDeletedAtIsNull(UUID id);
}
