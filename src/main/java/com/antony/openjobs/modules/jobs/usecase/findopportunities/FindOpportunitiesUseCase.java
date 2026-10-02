package com.antony.openjobs.modules.jobs.usecase.findopportunities;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.jobs.repository.JobRepository;
import com.antony.openjobs.modules.users.usecase.UserSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FindOpportunitiesUseCase {
    private final JobRepository jobRepository;

    @Transactional(readOnly = true)
    public IPaginationResponse<FindOpportunitiesResponse> execute(String search, int page, int size) {
        var pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 30),
                Sort.by(Sort.Direction.DESC, "createdAt", "id")
        );
        var jobs = jobRepository.findByDeletedAtIsNullAndPublishedBy_DeletedAtIsNullAndTitleContainingIgnoreCase(
                search.trim(), pageable
        );
        var response = jobs.map(job -> new FindOpportunitiesResponse(
                job.getId(), job.getTitle(), job.getDescription(), job.getCreatedAt(),
                UserSummaryResponse.from(job.getPublishedBy())
        ));

        return IPaginationResponse.from(response);
    }
}
