package com.antony.openjobs.modules.jobs.controller;

import com.antony.openjobs.common.api.ApiResponse;
import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.config.security.AuthenticatedUser;
import com.antony.openjobs.modules.jobs.usecase.create.CreateJobRequest;
import com.antony.openjobs.modules.jobs.usecase.create.CreateJobResponse;
import com.antony.openjobs.modules.jobs.usecase.create.CreateJobUseCase;
import com.antony.openjobs.modules.jobs.usecase.delete.DeleteJobResponse;
import com.antony.openjobs.modules.jobs.usecase.delete.DeleteJobUseCase;
import com.antony.openjobs.modules.jobs.usecase.findall.FindAllJobsProjection;
import com.antony.openjobs.modules.jobs.usecase.findall.FindAllJobsUseCase;
import com.antony.openjobs.modules.jobs.usecase.update.UpdateJobRequest;
import com.antony.openjobs.modules.jobs.usecase.update.UpdateJobResponse;
import com.antony.openjobs.modules.jobs.usecase.update.UpdateJobUseCase;
import com.antony.openjobs.modules.roles.usecase.findall.FindAllRolesProjection;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController()
@RequestMapping("/v1/jobs")
@RequiredArgsConstructor
public class JobController {
    private final FindAllJobsUseCase findAllJobsUseCase;
    private final CreateJobUseCase createJobUseCase;
    private final UpdateJobUseCase updateJobUseCaseJobUseCase;
    private final DeleteJobUseCase deleteJobUseCase;

    @GetMapping()
    public ResponseEntity<ApiResponse<IPaginationResponse<FindAllJobsProjection>>> findAll(
            Pageable pageable
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(findAllJobsUseCase.execute(pageable)));
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<CreateJobResponse>> create(
            @Valid @RequestBody CreateJobRequest request,
            @AuthenticationPrincipal AuthenticatedUser loggedUser
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        createJobUseCase.execute(request, loggedUser.userId())
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateJobResponse>> create(
            @PathVariable("id") UUID jobId,
            @Valid @RequestBody UpdateJobRequest request,
            @AuthenticationPrincipal AuthenticatedUser loggedUser
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        updateJobUseCaseJobUseCase.execute(jobId, request, loggedUser.userId())
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteJobResponse>> delete(
            @PathVariable("id") UUID jobId,
            @AuthenticationPrincipal AuthenticatedUser loggedUser
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success((
                        deleteJobUseCase.execute(jobId, loggedUser.userId()))
                ));
    }
}
