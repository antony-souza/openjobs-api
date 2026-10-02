package com.antony.openjobs.modules.comments.usecase.create;

import com.antony.openjobs.modules.comments.model.CommentEntity;
import com.antony.openjobs.modules.comments.repository.ICommentRepository;
import com.antony.openjobs.modules.comments.services.CommentValidationService;
import com.antony.openjobs.modules.comments.usecase.CommentResponse;
import com.antony.openjobs.modules.posts.services.PostValidationService;
import com.antony.openjobs.modules.users.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateCommentUseCase {
    private final PostValidationService postValidationService;
    private final IUserRepository userRepository;
    private final ICommentRepository commentRepository;
    private final CommentValidationService commentValidationService;

    @Transactional
    public CommentResponse execute(UUID postId, UUID userId, CreateCommentRequest request) {
        var post = postValidationService.findActivePost(postId);
        var parent = request.parentCommentId() == null ? null
                : commentValidationService.findActiveComment(postId, request.parentCommentId());
        var user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        var comment = new CommentEntity();
        comment.setPost(post);
        comment.setParentComment(parent);
        comment.setUser(user);
        comment.setContent(request.content().trim());

        return CommentResponse.from(commentRepository.saveAndFlush(comment));
    }
}
