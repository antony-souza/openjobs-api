package com.antony.openjobs.modules.applications.repository;

import com.antony.openjobs.common.repositories.IBaseRepository;
import com.antony.openjobs.modules.applications.model.ApplicationEntity;
import com.antony.openjobs.modules.applications.usecase.findall.FindAllApplicationsByCandidateIdProjection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends IBaseRepository<ApplicationEntity, UUID> {
    Page<FindAllApplicationsByCandidateIdProjection> findAllByCandidate_IdAndDeletedAtIsNull(
            UUID candidateId,
            Pageable pageable);
}