package com.antony.openjobs.modules.rolepermissions.usecase.create;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.antony.openjobs.modules.permissions.repository.IPermissionRepository;
import com.antony.openjobs.modules.rolepermissions.model.RolePermissionEntity;
import com.antony.openjobs.modules.rolepermissions.repository.IRolePermissionRepository;
import com.antony.openjobs.modules.roles.repository.IRoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateRolePermissionUseCase {
    private final IRolePermissionRepository rolePermissionRepository;
    private final IRoleRepository roleRepository;
    private final IPermissionRepository permissionRepository;

    public CreateRolePermissionResponse execute(CreateRolePermissionRequest request) {
        var roleEntity = roleRepository.findByIdAndDeletedAtIsNull(request.roleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        var permissionEntity = permissionRepository
                .findByIdAndDeletedAtIsNull(request.permissionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permission not found"));

        if (rolePermissionRepository.existsByRoleIdAndPermissionIdAndDeletedAtIsNull(
                request.roleId(),
                request.permissionId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Role permission already exists");
        }

        var rolePermission = new RolePermissionEntity();

        rolePermission.setRole(roleEntity);
        rolePermission.setPermission(permissionEntity);
        rolePermissionRepository.save(rolePermission);

        return new CreateRolePermissionResponse("Role permission created successfully!");
    }
}
