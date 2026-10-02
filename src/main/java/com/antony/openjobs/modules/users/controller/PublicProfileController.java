package com.antony.openjobs.modules.users.controller;

import com.antony.openjobs.common.api.ApiResponse;
import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.config.security.AuthenticatedUser;
import com.antony.openjobs.modules.posts.usecase.feed.FeedResponse;
import com.antony.openjobs.modules.posts.usecase.publicprofile.FindPublicProfilePostsUseCase;
import com.antony.openjobs.modules.users.usecase.publicprofile.FindPublicProfileUseCase;
import com.antony.openjobs.modules.users.usecase.publicprofile.PublicProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/profiles")
@RequiredArgsConstructor
public class PublicProfileController {
    private final FindPublicProfileUseCase findPublicProfileUseCase;
    private final FindPublicProfilePostsUseCase findPublicProfilePostsUseCase;

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<PublicProfileResponse>> profile(@PathVariable String username) {
        return ResponseEntity.ok(ApiResponse.success(findPublicProfileUseCase.execute(username)));
    }

    @GetMapping("/{username}/posts")
    public ResponseEntity<ApiResponse<IPaginationResponse<FeedResponse>>> posts(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                findPublicProfilePostsUseCase.execute(username, user == null ? null : user.userId(), page)
        ));
    }
}
