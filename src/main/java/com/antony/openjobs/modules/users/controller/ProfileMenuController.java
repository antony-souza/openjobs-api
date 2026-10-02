package com.antony.openjobs.modules.users.controller;

import com.antony.openjobs.common.api.ApiResponse;
import com.antony.openjobs.config.security.AuthenticatedUser;
import com.antony.openjobs.modules.users.usecase.findmenu.FindProfileMenuResponse;
import com.antony.openjobs.modules.users.usecase.findmenu.FindProfileMenuUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProfileMenuController {
    private final FindProfileMenuUseCase findProfileMenuUseCase;

    @GetMapping("/v1/users/me/menu")
    public ResponseEntity<ApiResponse<List<FindProfileMenuResponse>>> find(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(ApiResponse.success(findProfileMenuUseCase.execute(user.roleId())));
    }
}
