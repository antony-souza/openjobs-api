package com.antony.openjobs.modules.applications.repository;

import com.antony.openjobs.modules.applications.model.ApplicationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, UUID> {

    Optional<ApplicationEntity> findByIdAndDeletedAtIsNull(UUID id);

    Page<ApplicationEntity> findByCandidate_IdAndCandidate_DeletedAtIsNullAndJob_DeletedAtIsNullAndDeletedAtIsNull(
            UUID candidateId,
            Pageable pageable
    );
}