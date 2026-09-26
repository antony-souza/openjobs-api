package com.antony.openjobs.modules.permissions.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.antony.openjobs.common.api.ApiResponse;
import com.antony.openjobs.config.security.RequiresPermission;
import com.antony.openjobs.modules.permissions.model.Permission;
import com.antony.openjobs.modules.permissions.usecase.create.CreatePermissionRequest;
import com.antony.openjobs.modules.permissions.usecase.create.CreatePermissionResponse;
import com.antony.openjobs.modules.permissions.usecase.create.CreatePermissionUseCase;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final CreatePermissionUseCase createPermissionUseCase;

    @RequiresPermission(Permission.PERMISSION_CREATE)
    @PostMapping()
    public ResponseEntity<ApiResponse<CreatePermissionResponse>> create(
            @Valid @RequestBody CreatePermissionRequest request) {

        var response = createPermissionUseCase.execute(request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
