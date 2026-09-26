package com.antony.openjobs.modules.permissions.usecase.create;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.antony.openjobs.modules.permissions.model.PermissionEntity;
import com.antony.openjobs.modules.permissions.repository.PermissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreatePermissionUseCase {
    private final PermissionRepository permissionRepository;

    @Transactional
    public CreatePermissionResponse execute(CreatePermissionRequest createPermissionRequest) {
        if (permissionRepository.existsByCodeAndDeletedAtIsNull(createPermissionRequest.code().trim())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Permission with code already exists");
        }

        var permission = new PermissionEntity();

        permission.setName(createPermissionRequest.name().trim());
        permission.setCode(createPermissionRequest.code().trim());
        permission.setDescription(createPermissionRequest.description().trim());

        permissionRepository.save(permission);

        return new CreatePermissionResponse("Permission created successfully!");
    }
}
