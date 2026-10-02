package com.antony.openjobs.modules.comments.usecase.delete;

import com.antony.openjobs.modules.comments.model.CommentEntity;
import com.antony.openjobs.modules.comments.repository.ICommentRepository;
import com.antony.openjobs.modules.comments.services.CommentValidationService;
import com.antony.openjobs.modules.posts.services.PostValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteCommentUseCase {
    private final PostValidationService postValidationService;
    private final CommentValidationService commentValidationService;
    private final ICommentRepository commentRepository;

    @Transactional
    public DeleteCommentResponse execute(UUID postId, UUID commentId, UUID userId) {
        postValidationService.findActivePost(postId);
        var comment = commentValidationService.findOwnedComment(postId, commentId, userId);
        var deletedAt = LocalDateTime.now();
        var deleted = new ArrayList<CommentEntity>();
        var batch = List.of(comment);
        while (!batch.isEmpty()) {
            var ids = batch.stream().map(CommentEntity::getId).toList();
            batch.forEach(item -> item.setDeletedAt(deletedAt));
            deleted.addAll(batch);
            batch = commentRepository.findByParentComment_IdInAndDeletedAtIsNull(ids);
        }
        commentRepository.saveAll(deleted);
        return new DeleteCommentResponse("Comentário excluído com sucesso");
    }
}
