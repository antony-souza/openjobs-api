package com.antony.openjobs.modules.jobs.controller;

import com.antony.openjobs.common.api.ApiResponse;
import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.jobs.usecase.findopportunities.FindOpportunitiesResponse;
import com.antony.openjobs.modules.jobs.usecase.findopportunities.FindOpportunitiesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/opportunities")
@RequiredArgsConstructor
public class OpportunityController {
    private final FindOpportunitiesUseCase findOpportunitiesUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<IPaginationResponse<FindOpportunitiesResponse>>> find(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(findOpportunitiesUseCase.execute(search, page, size)));
    }
}
