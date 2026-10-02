package com.antony.openjobs.modules.comments.usecase.replies;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.comments.repository.ICommentRepository;
import com.antony.openjobs.modules.comments.services.CommentMappingService;
import com.antony.openjobs.modules.comments.services.CommentValidationService;
import com.antony.openjobs.modules.comments.usecase.CommentResponse;
import com.antony.openjobs.modules.posts.services.PostValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindCommentRepliesUseCase {
    private final PostValidationService postValidationService;
    private final CommentValidationService commentValidationService;
    private final ICommentRepository commentRepository;
    private final CommentMappingService commentMappingService;

    @Transactional(readOnly = true)
    public IPaginationResponse<CommentResponse> execute(UUID postId, UUID commentId, UUID viewerId, int page) {
        postValidationService.findActivePost(postId);
        commentValidationService.findActiveComment(postId, commentId);
        var pageable = PageRequest.of(Math.max(page, 0), 10, Sort.by(Sort.Direction.ASC, "createdAt", "id"));
        var replies = commentRepository.findByParentComment_IdAndDeletedAtIsNullAndUser_DeletedAtIsNull(commentId, pageable);
        return commentMappingService.map(replies, viewerId);
    }
}
