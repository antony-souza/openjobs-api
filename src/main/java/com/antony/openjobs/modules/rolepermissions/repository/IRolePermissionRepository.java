package com.antony.openjobs.modules.rolepermissions.repository;

import com.antony.openjobs.common.repositories.IBaseRepository;
import com.antony.openjobs.modules.rolepermissions.model.RolePermissionEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface IRolePermissionRepository extends IBaseRepository<RolePermissionEntity, UUID> {
    @Transactional(readOnly = true)
    boolean existsByRole_IdAndPermission_CodeAndDeletedAtIsNullAndRole_DeletedAtIsNullAndPermission_DeletedAtIsNull(
            UUID roleId,
            String permissionCode);

    boolean existsByRoleIdAndPermissionIdAndDeletedAtIsNull(
            UUID roleId,
            UUID permissionId);
}
