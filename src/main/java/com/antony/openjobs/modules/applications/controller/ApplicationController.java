package com.antony.openjobs.modules.applications.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.config.security.AuthenticatedUser;
import com.antony.openjobs.modules.applications.usecase.create.CreateApplicationRequest;
import com.antony.openjobs.modules.applications.usecase.create.CreateApplicationResponse;
import com.antony.openjobs.modules.applications.usecase.create.CreateApplicationUseCase;
import com.antony.openjobs.modules.applications.usecase.delete.DeleteApplicationResponse;
import com.antony.openjobs.modules.applications.usecase.delete.DeleteApplicationUseCase;
import com.antony.openjobs.modules.applications.usecase.findall.FindAllApplicationsByCandidateIdProjection;
import com.antony.openjobs.modules.applications.usecase.findall.FindAllApplicationsByCandidateIdUseCase;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

import com.antony.openjobs.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final FindAllApplicationsByCandidateIdUseCase findAllApplicationsByCandidateIdUseCase;
    private final CreateApplicationUseCase createApplicationUseCase;
    private final DeleteApplicationUseCase deleteApplicationUseCase;

    @GetMapping()
    public ResponseEntity<ApiResponse<IPaginationResponse<FindAllApplicationsByCandidateIdProjection>>> findAllByCandidateId(
            @AuthenticationPrincipal AuthenticatedUser loggedUser,
            Pageable pageable) {

        var response = findAllApplicationsByCandidateIdUseCase.execute(loggedUser.userId(), pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<CreateApplicationResponse>> create(
            @AuthenticationPrincipal AuthenticatedUser loggedUser,
            @Valid @RequestBody CreateApplicationRequest request) {

        var response = createApplicationUseCase.execute(loggedUser.userId(), request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<DeleteApplicationResponse>> delete(
            @AuthenticationPrincipal AuthenticatedUser loggedUser,
            @PathVariable UUID id) {

        var response = deleteApplicationUseCase.execute(id, loggedUser.userId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
