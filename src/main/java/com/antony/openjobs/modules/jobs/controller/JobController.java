package com.antony.openjobs.modules.jobs.controller;

import com.antony.openjobs.common.api.ApiResponse;
import com.antony.openjobs.config.security.AuthenticatedUser;
import com.antony.openjobs.modules.jobs.usecase.create.CreateJobRequest;
import com.antony.openjobs.modules.jobs.usecase.create.CreateJobResponse;
import com.antony.openjobs.modules.jobs.usecase.create.CreateJobUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/v1/jobs")
@RequiredArgsConstructor
public class JobController {
    private final CreateJobUseCase createJobUseCase;

    @PostMapping()
    public ResponseEntity<ApiResponse<CreateJobResponse>> create(
            @Valid
            @RequestBody CreateJobRequest request,
            @AuthenticationPrincipal AuthenticatedUser loggedUser
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        createJobUseCase.execute(request, loggedUser.userId())
                ));
    }
}
