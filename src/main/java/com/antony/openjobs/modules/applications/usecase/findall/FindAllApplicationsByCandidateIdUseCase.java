package com.antony.openjobs.modules.applications.usecase.findall;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.applications.repository.ApplicationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FindAllApplicationsByCandidateIdUseCase {
    private final ApplicationRepository applicationRepository;

    public IPaginationResponse<FindAllApplicationsByCandidateIdProjection> execute(UUID candidateId,
            Pageable pageable) {

        var page = applicationRepository
                .findAllByCandidate_IdAndDeletedAtIsNull(candidateId, pageable);

        return IPaginationResponse.from(page);
    }
}
