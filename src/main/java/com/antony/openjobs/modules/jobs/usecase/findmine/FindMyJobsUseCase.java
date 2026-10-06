package com.antony.openjobs.modules.jobs.usecase.findmine;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.jobs.repository.JobRepository;
import com.antony.openjobs.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindMyJobsUseCase {
    private final JobRepository jobRepository;

    @Transactional(readOnly = true)
    public IPaginationResponse<FindMyJobsResponse> execute(UUID userId, int page, int size) {
        var pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 30),
                Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        var jobs = jobRepository.findByPublishedBy_IdAndDeletedAtIsNull(userId, pageable);
        return IPaginationResponse.from(jobs.map(job -> new FindMyJobsResponse(
                job.getId(), job.getTitle(), job.getDescription(), DateTimeUtils.withServerOffset(job.getCreatedAt()))));
    }
}
