package com.antony.openjobs.modules.applications.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.config.security.AuthenticatedUser;
import com.antony.openjobs.modules.applications.usecase.findall.FindAllApplicationsByCandidateIdProjection;
import com.antony.openjobs.modules.applications.usecase.findall.FindAllApplicationsByCandidateIdUseCase;
import com.antony.openjobs.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final FindAllApplicationsByCandidateIdUseCase findAllApplicationsUseCase;

    @GetMapping()
    public ResponseEntity<ApiResponse<IPaginationResponse<FindAllApplicationsByCandidateIdProjection>>> findAll(
            @AuthenticationPrincipal AuthenticatedUser loggedUser,
            Pageable pageable) {

        var response = findAllApplicationsUseCase.execute(loggedUser.userId(), pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
