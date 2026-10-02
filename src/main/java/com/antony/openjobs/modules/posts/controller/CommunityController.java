package com.antony.openjobs.modules.posts.controller;

import com.antony.openjobs.common.api.ApiResponse;
import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.config.security.AuthenticatedUser;
import com.antony.openjobs.modules.comments.usecase.CommentResponse;
import com.antony.openjobs.modules.comments.usecase.create.CreateCommentRequest;
import com.antony.openjobs.modules.comments.usecase.create.CreateCommentUseCase;
import com.antony.openjobs.modules.comments.usecase.findall.FindAllPostCommentsUseCase;
import com.antony.openjobs.modules.likes.usecase.set.SetPostLikeRequest;
import com.antony.openjobs.modules.likes.usecase.set.SetPostLikeResponse;
import com.antony.openjobs.modules.likes.usecase.set.SetPostLikeUseCase;
import com.antony.openjobs.modules.posts.usecase.create.CreatePostRequest;
import com.antony.openjobs.modules.posts.usecase.create.CreatePostResponse;
import com.antony.openjobs.modules.posts.usecase.create.CreatePostUseCase;
import com.antony.openjobs.modules.posts.usecase.feed.FeedResponse;
import com.antony.openjobs.modules.posts.usecase.feed.FindCommunityFeedUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/v1/community")
@RequiredArgsConstructor
public class CommunityController {
    private final FindCommunityFeedUseCase findCommunityFeedUseCase;
    private final FindAllPostCommentsUseCase findAllPostCommentsUseCase;
    private final CreateCommentUseCase createCommentUseCase;
    private final SetPostLikeUseCase setPostLikeUseCase;
    private final CreatePostUseCase createPostUseCase;

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<IPaginationResponse<FeedResponse>>> feed(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var response = findCommunityFeedUseCase.execute(user.userId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CreatePostResponse>> create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @ModelAttribute CreatePostRequest request
    ) {
        var response = createPostUseCase.execute(request, user.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<IPaginationResponse<CommentResponse>>> comments(
            @PathVariable UUID postId,
            @RequestParam(defaultValue = "0") int page
    ) {
        var response = findAllPostCommentsUseCase.execute(postId, page);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> comment(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID postId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        var response = createCommentUseCase.execute(postId, user.userId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/posts/{postId}/like")
    public ResponseEntity<ApiResponse<SetPostLikeResponse>> like(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID postId,
            @RequestBody SetPostLikeRequest request
    ) {
        var response = setPostLikeUseCase.execute(postId, user.userId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
