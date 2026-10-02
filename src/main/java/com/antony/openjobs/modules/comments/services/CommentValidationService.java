package com.antony.openjobs.modules.comments.services;

import com.antony.openjobs.modules.comments.model.CommentEntity;
import com.antony.openjobs.modules.comments.repository.ICommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentValidationService {
    private final ICommentRepository commentRepository;

    public CommentEntity findActiveComment(UUID postId, UUID commentId) {
        return commentRepository.findByIdAndPost_IdAndDeletedAtIsNullAndUser_DeletedAtIsNull(commentId, postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário não encontrado nesta publicação"));
    }

    public CommentEntity findOwnedComment(UUID postId, UUID commentId, UUID userId) {
        var comment = findActiveComment(postId, commentId);
        if (!comment.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode alterar seus próprios comentários");
        }
        return comment;
    }
}
