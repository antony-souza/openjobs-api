package com.antony.openjobs.modules.comments.services;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.comments.model.CommentEntity;
import com.antony.openjobs.modules.comments.repository.ICommentRepository;
import com.antony.openjobs.modules.comments.repository.projection.CommentCount;
import com.antony.openjobs.modules.commentlikes.repository.ICommentLikeRepository;
import com.antony.openjobs.modules.comments.usecase.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.UUID;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentMappingService {
    private final ICommentRepository commentRepository;
    private final ICommentLikeRepository commentLikeRepository;

    public IPaginationResponse<CommentResponse> map(Page<CommentEntity> comments, UUID viewerId) {
        if (comments.isEmpty()) {
            return IPaginationResponse.from(comments.map(CommentResponse::from));
        }
        var ids = comments.stream().map(CommentEntity::getId).toList();
        var counts = commentRepository.countRepliesForComments(ids).stream()
                .collect(Collectors.toMap(CommentCount::getCommentId, CommentCount::getTotal));
        var likes = commentLikeRepository.countForComments(ids).stream()
                .collect(Collectors.toMap(CommentCount::getCommentId, CommentCount::getTotal));
        Set<UUID> liked = viewerId == null ? Set.of() : commentLikeRepository.findByComment_IdInAndUser_IdAndDeletedAtIsNull(ids, viewerId).stream()
                .map(like -> like.getComment().getId()).collect(Collectors.toSet());
        return IPaginationResponse.from(comments.map(comment -> CommentResponse.from(
                comment, counts.getOrDefault(comment.getId(), 0L),
                likes.getOrDefault(comment.getId(), 0L), liked.contains(comment.getId())
        )));
    }
}
