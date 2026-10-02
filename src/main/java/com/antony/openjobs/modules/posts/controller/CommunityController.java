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
import com.antony.openjobs.modules.posts.usecase.profile.FindProfilePostsUseCase;
import com.antony.openjobs.modules.posts.usecase.update.UpdatePostRequest;
import com.antony.openjobs.modules.posts.usecase.update.UpdatePostResponse;
import com.antony.openjobs.modules.posts.usecase.update.UpdatePostUseCase;
import com.antony.openjobs.modules.posts.usecase.delete.DeletePostResponse;
import com.antony.openjobs.modules.posts.usecase.delete.DeletePostUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    private final FindProfilePostsUseCase findProfilePostsUseCase;
    private final UpdatePostUseCase updatePostUseCase;
    private final DeletePostUseCase deletePostUseCase;

    @GetMapping("/posts/me")
    public ResponseEntity<ApiResponse<IPaginationResponse<FeedResponse>>> myPosts(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var response = findProfilePostsUseCase.execute(user.userId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping(value = "/posts/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UpdatePostResponse>> update(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID postId,
            @Valid @ModelAttribute UpdatePostRequest request
    ) {
        var response = updatePostUseCase.execute(postId, user.userId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<DeletePostResponse>> delete(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID postId
    ) {
        var response = deletePostUseCase.execute(postId, user.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

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
