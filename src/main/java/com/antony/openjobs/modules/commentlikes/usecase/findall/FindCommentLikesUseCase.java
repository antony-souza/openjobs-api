package com.antony.openjobs.modules.commentlikes.usecase.findall;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.commentlikes.repository.ICommentLikeRepository;
import com.antony.openjobs.modules.posts.services.PostValidationService;
import com.antony.openjobs.modules.users.usecase.UserSummaryResponse;
import com.antony.openjobs.modules.comments.services.CommentValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindCommentLikesUseCase {
    private final ICommentLikeRepository likeRepository;
    private final PostValidationService postValidationService;
    private final CommentValidationService commentValidationService;

    @Transactional(readOnly = true)
    public IPaginationResponse<UserSummaryResponse> execute(UUID postId, UUID commentId, int page) {
        postValidationService.findActivePost(postId);
        commentValidationService.findActiveComment(postId, commentId);
        var pageable = PageRequest.of(Math.max(page, 0), 20, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        var likes = likeRepository.findByComment_IdAndDeletedAtIsNullAndUser_DeletedAtIsNull(commentId, pageable);
        return IPaginationResponse.from(likes.map(like -> UserSummaryResponse.from(like.getUser())));
    }
}
