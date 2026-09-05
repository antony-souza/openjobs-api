package com.antony.openjobs.modules.auth.controller;

import com.antony.openjobs.modules.auth.signin.SignInRequest;
import com.antony.openjobs.modules.auth.signin.SignInResponse;
import com.antony.openjobs.modules.auth.signin.SignInUseCase;
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

    @PostMapping("/sign-in")
    public SignInResponse signIn(@Valid @RequestBody SignInRequest request) {
        return signInUseCase.execute(request);
    }
}
