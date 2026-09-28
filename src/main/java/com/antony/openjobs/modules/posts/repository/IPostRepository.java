package com.antony.openjobs.modules.posts.repository;

import java.util.UUID;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.antony.openjobs.common.repositories.IBaseRepository;
import com.antony.openjobs.modules.posts.model.PostEntity;
import com.antony.openjobs.modules.posts.usecase.findall.FindAllPostsByUserIdProjection;

@Repository
public interface IPostRepository extends IBaseRepository<PostEntity, UUID> {
    Page<FindAllPostsByUserIdProjection> findAllByUser_IdAndDeletedAtIsNull(UUID userId, Pageable pageable);

    Optional<PostEntity> findByIdAndDeletedAtIsNull(UUID postId);
}
