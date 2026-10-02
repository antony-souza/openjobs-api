package com.antony.openjobs.modules.comments.usecase;

import com.antony.openjobs.modules.comments.model.CommentEntity;
import com.antony.openjobs.modules.users.usecase.UserSummaryResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentResponse(
        UUID id, String content, LocalDateTime createdAt, UserSummaryResponse author,
        UUID parentCommentId, long repliesCount, long likesCount, boolean liked
) {
    public static CommentResponse from(CommentEntity comment) {
        return from(comment, 0, 0, false);
    }

    public static CommentResponse from(CommentEntity comment, long repliesCount, long likesCount, boolean liked) {
        return new CommentResponse(
                comment.getId(), comment.getContent(), comment.getCreatedAt(), UserSummaryResponse.from(comment.getUser()),
                comment.getParentComment() == null ? null : comment.getParentComment().getId(),
                repliesCount, likesCount, liked
        );
    }
}
