package com.antony.openjobs.modules.users.controller;

import com.antony.openjobs.common.api.ApiResponse;
import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.config.security.RequiresPermission;
import com.antony.openjobs.modules.permissions.model.Permission;
import com.antony.openjobs.modules.users.repository.UserRepository;
import com.antony.openjobs.modules.users.usecase.delete.DeleteUserResponse;
import com.antony.openjobs.modules.users.usecase.delete.DeleteUserUseCase;
import com.antony.openjobs.modules.users.usecase.findall.FindAllUsersProjection;
import com.antony.openjobs.modules.users.usecase.update.UpdateUserRequest;
import com.antony.openjobs.modules.users.usecase.update.UpdateUserResponse;
import com.antony.openjobs.modules.users.usecase.update.UpdateUserUseCase;
import com.antony.openjobs.services.pagination.PaginationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    private final UserRepository userRepository;
    private final PaginationService paginationService;

    @RequiresPermission(Permission.USER_READ)
    @GetMapping()
    public ResponseEntity<ApiResponse<IPaginationResponse<FindAllUsersProjection>>> findAll(
            Pageable pageable
    ) {
        var response = paginationService.execute(
                userRepository,
                pageable,
                FindAllUsersProjection.class
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @RequiresPermission(Permission.USER_UPDATE)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateUserResponse>> update(
            @PathVariable("id") UUID userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updateUserUseCase.execute(userId, updateUserRequest)));
    }

    @RequiresPermission(Permission.USER_DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteUserResponse>> update(
            @PathVariable("id") UUID userId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(deleteUserUseCase.execute(userId)));
    }
}
