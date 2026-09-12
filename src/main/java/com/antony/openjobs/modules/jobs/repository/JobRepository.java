package com.antony.openjobs.modules.jobs.repository;

import com.antony.openjobs.modules.jobs.model.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, UUID> {
    Optional<JobEntity> findByTitle(String title);
}
