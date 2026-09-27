package com.antony.openjobs.modules.rolepermissions.controller;

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
import com.antony.openjobs.modules.rolepermissions.repository.RolePermissionRepository;
import com.antony.openjobs.modules.rolepermissions.usecase.create.CreateRolePermissionRequest;
import com.antony.openjobs.modules.rolepermissions.usecase.create.CreateRolePermissionResponse;
import com.antony.openjobs.modules.rolepermissions.usecase.create.CreateRolePermissionUseCase;
import com.antony.openjobs.modules.rolepermissions.usecase.findall.FindAllRolePermissionsProjection;
import com.antony.openjobs.services.pagination.PaginationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/role-permissions")
@RequiredArgsConstructor
public class RolePermissionsController {
    private final CreateRolePermissionUseCase createRolePermissionUseCase;
    private final RolePermissionRepository rolePermissionRepository;
    private final PaginationService paginationService;

    @RequiresPermission(Permission.ROLEPERMISSION_READ)
    @GetMapping()
    public ResponseEntity<ApiResponse<IPaginationResponse<FindAllRolePermissionsProjection>>> findAll(
            Pageable pageable) {

        var response = paginationService.execute(
                rolePermissionRepository,
                pageable,
                FindAllRolePermissionsProjection.class);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @RequiresPermission(Permission.ROLEPERMISSION_CREATE)
    @PostMapping()
    public ResponseEntity<ApiResponse<CreateRolePermissionResponse>> create(
            @Valid @RequestBody CreateRolePermissionRequest request) {

        var response = createRolePermissionUseCase.execute(request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
