package com.antony.openjobs.modules.comments.usecase.findall;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.comments.repository.ICommentRepository;
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
public class FindAllPostCommentsUseCase {
    private final PostValidationService postValidationService;
    private final ICommentRepository commentRepository;

    @Transactional(readOnly = true)
    public IPaginationResponse<CommentResponse> execute(UUID postId, int page) {
        postValidationService.findActivePost(postId);
        var pageable = PageRequest.of(Math.max(page, 0), 10, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        var comments = commentRepository.findByPost_IdAndDeletedAtIsNullAndUser_DeletedAtIsNull(postId, pageable);

        return IPaginationResponse.from(comments.map(CommentResponse::from));
    }
}
