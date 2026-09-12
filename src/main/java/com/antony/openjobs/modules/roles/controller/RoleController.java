package com.antony.openjobs.modules.roles.controller;

import com.antony.openjobs.common.api.ApiResponse;
import com.antony.openjobs.modules.roles.usecase.create.CreateRoleRequest;
import com.antony.openjobs.modules.roles.usecase.create.CreateRoleResponse;
import com.antony.openjobs.modules.roles.usecase.create.CreateRoleUseCase;
import com.antony.openjobs.modules.roles.usecase.update.UpdateRoleRequest;
import com.antony.openjobs.modules.roles.usecase.update.UpdateRoleResponse;
import com.antony.openjobs.modules.roles.usecase.update.UpdateRoleUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController()
@RequestMapping("/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final CreateRoleUseCase createRoleUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;

    @PostMapping()
    public ResponseEntity<ApiResponse<CreateRoleResponse>> create(
            @Valid @RequestBody CreateRoleRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createRoleUseCase.execute(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateRoleResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoleRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updateRoleUseCase.execute(id, request)));
    }
}
