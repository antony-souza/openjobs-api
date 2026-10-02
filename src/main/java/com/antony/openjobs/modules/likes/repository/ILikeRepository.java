package com.antony.openjobs.modules.likes.repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

import com.antony.openjobs.common.repositories.IBaseRepository;
import com.antony.openjobs.modules.likes.model.LikeEntity;
import com.antony.openjobs.modules.posts.repository.projection.PostCount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ILikeRepository extends IBaseRepository<LikeEntity, UUID> {
    @Query("""
            select l.post.id as postId, count(l) as total
            from LikeEntity l
            where l.post.id in :ids and l.deletedAt is null and l.user.deletedAt is null
            group by l.post.id
            """)
    List<PostCount> countForPosts(@Param("ids") List<UUID> ids);

    List<LikeEntity> findByPost_IdInAndUser_IdAndDeletedAtIsNull(List<UUID> postIds, UUID userId);

    Long countByPost_IdAndDeletedAtIsNull(UUID postId);

    boolean existsByPost_IdAndUser_IdAndDeletedAtIsNull(UUID postId, UUID userId);

    boolean existsByPost_IdAndUser_IdAndDeletedAtIsNotNull(UUID postId, UUID userId);

    Optional<LikeEntity> findByUser_IdAndPost_Id(UUID userId, UUID postId);
}
