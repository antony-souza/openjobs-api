package com.antony.openjobs.modules.jobs.usecase.findall;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.jobs.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindAllJobsUseCase {
    private final JobRepository jobRepository;

    public IPaginationResponse<FindAllJobsProjection> execute(Pageable pageable) {
        Page<FindAllJobsProjection> page = jobRepository.findAllByDeletedAtIsNull(pageable);

        return IPaginationResponse.from(page);
    }
}
