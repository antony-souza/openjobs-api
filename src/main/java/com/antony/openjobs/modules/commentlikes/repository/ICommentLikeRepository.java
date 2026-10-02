package com.antony.openjobs.modules.commentlikes.repository;

import com.antony.openjobs.common.repositories.IBaseRepository;
import com.antony.openjobs.modules.commentlikes.model.CommentLikeEntity;
import com.antony.openjobs.modules.comments.repository.projection.CommentCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ICommentLikeRepository extends IBaseRepository<CommentLikeEntity, UUID> {
    Optional<CommentLikeEntity> findByUser_IdAndComment_Id(UUID userId, UUID commentId);

    List<CommentLikeEntity> findByComment_IdInAndUser_IdAndDeletedAtIsNull(List<UUID> commentIds, UUID userId);

    @EntityGraph(attributePaths = "user")
    Page<CommentLikeEntity> findByComment_IdAndDeletedAtIsNullAndUser_DeletedAtIsNull(UUID commentId, Pageable pageable);

    @Query("""
            select l.comment.id as commentId, count(l) as total
            from CommentLikeEntity l
            where l.comment.id in :ids and l.deletedAt is null and l.user.deletedAt is null
            group by l.comment.id
            """)
    List<CommentCount> countForComments(@Param("ids") List<UUID> commentIds);
}
