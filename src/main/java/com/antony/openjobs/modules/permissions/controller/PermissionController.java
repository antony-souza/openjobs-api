package com.antony.openjobs.modules.permissions.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.antony.openjobs.common.api.ApiResponse;
import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.config.security.RequiresPermission;
import com.antony.openjobs.modules.permissions.model.Permission;
import com.antony.openjobs.modules.permissions.repository.PermissionRepository;
import com.antony.openjobs.modules.permissions.usecase.create.CreatePermissionRequest;
import com.antony.openjobs.modules.permissions.usecase.create.CreatePermissionResponse;
import com.antony.openjobs.modules.permissions.usecase.create.CreatePermissionUseCase;
import com.antony.openjobs.modules.permissions.usecase.findall.FindAllPermissionsProjection;
import com.antony.openjobs.services.pagination.PaginationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final CreatePermissionUseCase createPermissionUseCase;
    private final PermissionRepository permissionRepository;
    private final PaginationService paginationService;

    @RequiresPermission(Permission.PERMISSION_CREATE)
    @PostMapping()
    public ResponseEntity<ApiResponse<CreatePermissionResponse>> create(
            @Valid @RequestBody CreatePermissionRequest request) {

        var response = createPermissionUseCase.execute(request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @RequiresPermission(Permission.PERMISSION_READ)
    @GetMapping()
    public ResponseEntity<ApiResponse<IPaginationResponse<FindAllPermissionsProjection>>> findAll(Pageable pageable) {
        var response = paginationService.execute(
                permissionRepository,
                pageable,
                FindAllPermissionsProjection.class);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}