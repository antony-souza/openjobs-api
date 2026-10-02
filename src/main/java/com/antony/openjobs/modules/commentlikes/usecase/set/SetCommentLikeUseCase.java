package com.antony.openjobs.modules.commentlikes.usecase.set;

import com.antony.openjobs.modules.commentlikes.model.CommentLikeEntity;
import com.antony.openjobs.modules.commentlikes.repository.ICommentLikeRepository;
import com.antony.openjobs.modules.comments.services.CommentValidationService;
import com.antony.openjobs.modules.posts.services.PostValidationService;
import com.antony.openjobs.modules.users.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SetCommentLikeUseCase {
    private final PostValidationService postValidationService;
    private final CommentValidationService commentValidationService;
    private final ICommentLikeRepository commentLikeRepository;
    private final IUserRepository userRepository;

    @Transactional
    public SetCommentLikeResponse execute(UUID postId, UUID commentId, UUID userId, SetCommentLikeRequest request) {
        postValidationService.findActivePost(postId);
        var comment = commentValidationService.findActiveComment(postId, commentId);
        var user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        var existing = commentLikeRepository.findByUser_IdAndComment_Id(userId, commentId);
        if (existing.isEmpty() && !request.liked()) {
            return new SetCommentLikeResponse(false);
        }
        var like = existing.orElseGet(() -> {
            var created = new CommentLikeEntity();
            created.setComment(comment);
            created.setUser(user);
            return created;
        });
        like.setDeletedAt(request.liked() ? null : LocalDateTime.now());
        commentLikeRepository.save(like);
        return new SetCommentLikeResponse(request.liked());
    }
}
