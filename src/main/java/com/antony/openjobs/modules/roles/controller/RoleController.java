package com.antony.openjobs.modules.roles.controller;

import com.antony.openjobs.common.api.ApiResponse;
import com.antony.openjobs.modules.roles.usecase.create.CreateRoleRequest;
import com.antony.openjobs.modules.roles.usecase.create.CreateRoleResponse;
import com.antony.openjobs.modules.roles.usecase.create.CreateRoleUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final CreateRoleUseCase createRoleUseCase;

    @PostMapping()
    public ResponseEntity<ApiResponse<CreateRoleResponse>> create(
            @Valid @RequestBody CreateRoleRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createRoleUseCase.execute(request)));
    }
}
