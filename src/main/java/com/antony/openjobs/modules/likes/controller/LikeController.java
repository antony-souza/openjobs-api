package com.antony.openjobs.modules.likes.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.antony.openjobs.common.api.ApiResponse;
import com.antony.openjobs.config.security.AuthenticatedUser;
import com.antony.openjobs.modules.likes.usecase.upsert.UpsertLikeResponse;
import com.antony.openjobs.modules.likes.usecase.upsert.UpsertLikeUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/likes")
@RequiredArgsConstructor
public class LikeController {

    private final UpsertLikeUseCase upsertLikeUseCase;

    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<UpsertLikeResponse>> upsert(
            @PathVariable UUID postId,
            @AuthenticationPrincipal AuthenticatedUser loggedUser) {
        var response = upsertLikeUseCase.execute(postId, loggedUser.userId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
