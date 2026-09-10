package com.antony.openjobs.modules.auth.controller;

import com.antony.openjobs.common.api.ApiResponse;
import com.antony.openjobs.modules.auth.signin.SignInRequest;
import com.antony.openjobs.modules.auth.signin.SignInResponse;
import com.antony.openjobs.modules.auth.signin.SignInUseCase;
import com.antony.openjobs.modules.auth.signup.SignUpRequest;
import com.antony.openjobs.modules.auth.signup.SignUpResponse;
import com.antony.openjobs.modules.auth.signup.SignUpUseCase;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final SignInUseCase signInUseCase;
    private final SignUpUseCase signUpUseCase;

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<SignInResponse>> signIn(@Valid @RequestBody SignInRequest request) {
        return ResponseEntity.ok(ApiResponse.success(signInUseCase.execute(request)));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<SignUpResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(signUpUseCase.execute(request)));
    }
}
