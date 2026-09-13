package com.antony.openjobs.modules.users.controller;

import com.antony.openjobs.common.api.ApiResponse;
import com.antony.openjobs.modules.jobs.usecase.create.CreateJobUseCase;
import com.antony.openjobs.modules.roles.usecase.update.UpdateRoleRequest;
import com.antony.openjobs.modules.roles.usecase.update.UpdateRoleResponse;
import com.antony.openjobs.modules.users.usecase.delete.DeleteUserResponse;
import com.antony.openjobs.modules.users.usecase.delete.DeleteUserUseCase;
import com.antony.openjobs.modules.users.usecase.update.UpdateUserRequest;
import com.antony.openjobs.modules.users.usecase.update.UpdateUserResponse;
import com.antony.openjobs.modules.users.usecase.update.UpdateUserUseCase;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@AllArgsConstructor
public class UserController {
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateUserResponse>> update(
            @PathVariable("id") UUID userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updateUserUseCase.execute(userId, updateUserRequest)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteUserResponse>> update(
            @PathVariable("id") UUID userId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(deleteUserUseCase.execute(userId)));
    }
}
