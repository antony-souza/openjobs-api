package com.antony.openjobs.modules.jobs.repository;

import com.antony.openjobs.common.repositories.IBaseRepository;
import com.antony.openjobs.modules.jobs.model.JobEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends IBaseRepository<JobEntity, UUID> {
    Page<JobEntity> findByPublishedBy_IdAndDeletedAtIsNull(UUID publishedById, Pageable pageable);

    @EntityGraph(attributePaths = "publishedBy")
    Page<JobEntity> findByDeletedAtIsNullAndPublishedBy_DeletedAtIsNullAndTitleContainingIgnoreCase(String title, Pageable pageable);

    Optional<JobEntity> findByIdAndPublishedBy_IdAndDeletedAtIsNull(UUID id, UUID publishedById);

    boolean existsByTitleAndPublishedBy_IdAndDeletedAtIsNull(String title, UUID publishedById);
    Optional<JobEntity> findByIdAndDeletedAtIsNull(UUID id);
}
