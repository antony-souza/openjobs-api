package com.antony.openjobs.modules.posts.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.antony.openjobs.common.api.ApiResponse;
import com.antony.openjobs.config.security.AuthenticatedUser;
import com.antony.openjobs.config.security.RequiresPermission;
import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.permissions.model.Permission;
import com.antony.openjobs.modules.posts.usecase.create.CreatePostRequest;
import com.antony.openjobs.modules.posts.usecase.create.CreatePostResponse;
import com.antony.openjobs.modules.posts.usecase.create.CreatePostUseCase;
import com.antony.openjobs.modules.posts.usecase.findall.FindAllPostsByUserIdResponse;
import com.antony.openjobs.modules.posts.usecase.findall.FindAllPostsByUserIdUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final CreatePostUseCase createPostUseCase;
    private final FindAllPostsByUserIdUseCase findAllPostsByUserIdUseCase;

    @RequiresPermission(Permission.POST_READ)
    @GetMapping
    public ResponseEntity<ApiResponse<IPaginationResponse<FindAllPostsByUserIdResponse>>> findAll(
            @AuthenticationPrincipal AuthenticatedUser loggedUser,
            Pageable pageable) {

        var response = findAllPostsByUserIdUseCase.execute(loggedUser.userId(), pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @RequiresPermission(Permission.POST_CREATE)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CreatePostResponse>> create(
            @Valid @ModelAttribute CreatePostRequest request,
            @AuthenticationPrincipal AuthenticatedUser loggedUser) {
        var response = createPostUseCase.execute(request, loggedUser.userId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }
}
