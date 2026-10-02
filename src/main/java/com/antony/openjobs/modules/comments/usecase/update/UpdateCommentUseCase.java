package com.antony.openjobs.modules.comments.usecase.update;

import com.antony.openjobs.modules.comments.repository.ICommentRepository;
import com.antony.openjobs.modules.comments.services.CommentValidationService;
import com.antony.openjobs.modules.posts.services.PostValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateCommentUseCase {
    private final PostValidationService postValidationService;
    private final CommentValidationService commentValidationService;
    private final ICommentRepository commentRepository;

    @Transactional
    public UpdateCommentResponse execute(UUID postId, UUID commentId, UUID userId, UpdateCommentRequest request) {
        postValidationService.findActivePost(postId);
        var comment = commentValidationService.findOwnedComment(postId, commentId, userId);
        var content = request.content() == null ? "" : request.content().trim();
        if (content.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Escreva um comentário");
        }
        comment.setContent(content);
        commentRepository.save(comment);
        return new UpdateCommentResponse("Comentário atualizado com sucesso");
    }
}
