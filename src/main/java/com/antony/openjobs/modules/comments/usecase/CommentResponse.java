package com.antony.openjobs.modules.comments.usecase;

import com.antony.openjobs.modules.comments.model.CommentEntity;
import com.antony.openjobs.modules.users.usecase.UserSummaryResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentResponse(UUID id, String content, LocalDateTime createdAt, UserSummaryResponse author) {
    public static CommentResponse from(CommentEntity comment) {
        return new CommentResponse(
                comment.getId(), comment.getContent(), comment.getCreatedAt(), UserSummaryResponse.from(comment.getUser())
        );
    }
}
