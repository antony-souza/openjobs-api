package com.antony.openjobs.modules.jobs.usecase.capabilities;

import com.antony.openjobs.modules.permissions.model.Permission;
import com.antony.openjobs.modules.rolepermissions.repository.IRolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindJobCapabilitiesUseCase {
    private final IRolePermissionRepository rolePermissionRepository;

    @Transactional(readOnly = true)
    public FindJobCapabilitiesResponse execute(UUID roleId) {
        return new FindJobCapabilitiesResponse(rolePermissionRepository
                .existsByRole_IdAndPermission_CodeAndDeletedAtIsNullAndRole_DeletedAtIsNullAndPermission_DeletedAtIsNull(
                        roleId, Permission.JOB_CREATE.getCode()));
    }
}
