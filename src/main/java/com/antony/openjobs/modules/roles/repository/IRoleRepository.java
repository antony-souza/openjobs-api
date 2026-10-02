package com.antony.openjobs.modules.roles.repository;

import com.antony.openjobs.common.repositories.IBaseRepository;
import com.antony.openjobs.modules.roles.model.RoleEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IRoleRepository extends IBaseRepository<RoleEntity, UUID> {
    Optional<RoleEntity> findByCode(String code);

    Optional<RoleEntity> findByCodeAndDeletedAtIsNull(String code);

    Optional<RoleEntity> findByIdAndDeletedAtIsNull(UUID id);

    boolean existsByCodeAndDeletedAtIsNull(String code);

    boolean existsByCodeAndIdNotAndDeletedAtIsNull(String code, UUID id);
}
