package com.antony.openjobs.modules.jobs.repository;

import com.antony.openjobs.common.repositories.IBaseRepository;
import com.antony.openjobs.modules.jobs.model.JobEntity;
import com.antony.openjobs.modules.jobs.usecase.findall.FindAllJobsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends IBaseRepository<JobEntity, UUID> {
    Optional<JobEntity> findByIdAndPublishedBy_IdAndDeletedAtIsNull(UUID id, UUID publishedById);

    boolean existsByTitleAndPublishedBy_IdAndDeletedAtIsNull(String title, UUID publishedById);
}
