package com.antony.openjobs.modules.comments.usecase;

import com.antony.openjobs.modules.comments.model.CommentEntity;
import com.antony.openjobs.modules.users.usecase.UserSummaryResponse;
import com.antony.openjobs.utils.DateTimeUtils;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CommentResponse(
        UUID id, String content, OffsetDateTime createdAt, UserSummaryResponse author,
        UUID parentCommentId, long repliesCount, long likesCount, boolean liked
) {
    public static CommentResponse from(CommentEntity comment) {
        return from(comment, 0, 0, false);
    }

    public static CommentResponse from(CommentEntity comment, long repliesCount, long likesCount, boolean liked) {
        return new CommentResponse(
                comment.getId(), comment.getContent(), DateTimeUtils.withServerOffset(comment.getCreatedAt()), UserSummaryResponse.from(comment.getUser()),
                comment.getParentComment() == null ? null : comment.getParentComment().getId(),
                repliesCount, likesCount, liked
        );
    }
}
