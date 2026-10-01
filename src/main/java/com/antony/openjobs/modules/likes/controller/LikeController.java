package com.antony.openjobs.modules.likes.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.antony.openjobs.common.api.ApiResponse;
import com.antony.openjobs.config.security.AuthenticatedUser;
import com.antony.openjobs.config.security.RequiresPermission;
import com.antony.openjobs.modules.permissions.model.Permission;
import com.antony.openjobs.modules.likes.usecase.LikeResponse;
import com.antony.openjobs.modules.likes.usecase.create.CreateLikeUseCase;
import com.antony.openjobs.modules.likes.usecase.update.UpdateLikeUseCase;
import com.antony.openjobs.modules.likes.usecase.delete.DeleteLikeUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/likes")
@RequiredArgsConstructor
public class LikeController {

    private final CreateLikeUseCase createLikeUseCase;
    private final UpdateLikeUseCase updateLikeUseCase;
    private final DeleteLikeUseCase deleteLikeUseCase;

    @RequiresPermission(Permission.LIKE_CREATE)
    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<LikeResponse>> create(
            @PathVariable UUID postId,
            @AuthenticationPrincipal AuthenticatedUser loggedUser) {
        var response = createLikeUseCase.execute(postId, loggedUser.userId());

        return ResponseEntity.status(201).body(ApiResponse.success(response));
    }

    @RequiresPermission(Permission.LIKE_UPDATE)
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<LikeResponse>> update(
            @PathVariable UUID postId,
            @AuthenticationPrincipal AuthenticatedUser loggedUser) {
        var response = updateLikeUseCase.execute(postId, loggedUser.userId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @RequiresPermission(Permission.LIKE_DELETE)
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<LikeResponse>> delete(
            @PathVariable UUID postId,
            @AuthenticationPrincipal AuthenticatedUser loggedUser) {
        var response = deleteLikeUseCase.execute(postId, loggedUser.userId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
