package com.antony.openjobs.modules.permissions.usecase.update;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.antony.openjobs.modules.permissions.repository.PermissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdatePermissionUseCase {
    private final PermissionRepository permissionRepository;

    @Transactional
    public UpdatePermissionResponse execute(UUID id, UpdatePermissionRequest request) {
        var permission = permissionRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permission not found"));

        permission.setName(request.name());
        permission.setCode(request.code());
        permission.setDescription(request.description());

        permissionRepository.save(permission);

        return new UpdatePermissionResponse("Permission updated successfully");
    }
}
