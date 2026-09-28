package com.antony.openjobs.modules.likes.repository;

import java.util.Optional;
import java.util.UUID;

import com.antony.openjobs.common.repositories.IBaseRepository;
import com.antony.openjobs.modules.likes.model.LikeEntity;

public interface ILikeRepository extends IBaseRepository<LikeEntity, UUID> {
    Long countByPost_IdAndDeletedAtIsNull(UUID postId);

    boolean existsByPost_IdAndUser_IdAndDeletedAtIsNull(UUID postId, UUID userId);

    boolean existsByPost_IdAndUser_IdAndDeletedAtIsNotNull(UUID postId, UUID userId);

    Optional<LikeEntity> findByUser_IdAndPost_Id(UUID userId, UUID postId);
}
