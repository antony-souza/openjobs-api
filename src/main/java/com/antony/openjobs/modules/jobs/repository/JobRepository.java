package com.antony.openjobs.modules.jobs.repository;

import com.antony.openjobs.common.repositories.IBaseRepository;
import com.antony.openjobs.modules.jobs.model.JobEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends IBaseRepository<JobEntity, UUID> {
    Optional<JobEntity> findByIdAndPublishedBy_IdAndDeletedAtIsNull(UUID id, UUID publishedById);

    boolean existsByTitleAndPublishedBy_IdAndDeletedAtIsNull(String title, UUID publishedById);
    Optional<JobEntity> findByIdAndDeletedAtIsNull(UUID id);
}
