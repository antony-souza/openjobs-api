package com.antony.openjobs.modules.comments.repository;

import com.antony.openjobs.common.repositories.IBaseRepository;
import com.antony.openjobs.modules.comments.model.CommentEntity;
import com.antony.openjobs.modules.posts.repository.projection.PostCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface ICommentRepository extends IBaseRepository<CommentEntity, UUID> {
    @EntityGraph(attributePaths = "user")
    Page<CommentEntity> findByPost_IdAndDeletedAtIsNullAndUser_DeletedAtIsNull(UUID postId, Pageable pageable);

    @Query("""
            select c.post.id as postId, count(c) as total
            from CommentEntity c
            where c.post.id in :ids and c.deletedAt is null and c.user.deletedAt is null
            group by c.post.id
            """)
    List<PostCount> countForPosts(@Param("ids") List<UUID> ids);
}
