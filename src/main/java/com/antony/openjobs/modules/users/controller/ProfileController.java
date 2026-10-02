package com.antony.openjobs.modules.users.controller;

import com.antony.openjobs.common.api.ApiResponse;
import com.antony.openjobs.config.security.AuthenticatedUser;
import com.antony.openjobs.modules.users.usecase.ProfileResponse;
import com.antony.openjobs.modules.users.usecase.findprofile.FindProfileUseCase;
import com.antony.openjobs.modules.users.usecase.updateprofile.UpdateProfileRequest;
import com.antony.openjobs.modules.users.usecase.updateprofile.UpdateProfileUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users/me")
@RequiredArgsConstructor
public class ProfileController {
    private final FindProfileUseCase findProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> find(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(ApiResponse.success(findProfileUseCase.execute(user.userId())));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProfileResponse>> update(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @ModelAttribute UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(updateProfileUseCase.execute(user.userId(), request)));
    }
}
