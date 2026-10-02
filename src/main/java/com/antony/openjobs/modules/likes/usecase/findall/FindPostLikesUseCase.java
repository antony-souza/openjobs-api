package com.antony.openjobs.modules.likes.usecase.findall;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.likes.repository.ILikeRepository;
import com.antony.openjobs.modules.posts.services.PostValidationService;
import com.antony.openjobs.modules.users.usecase.UserSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindPostLikesUseCase {
    private final ILikeRepository likeRepository;
    private final PostValidationService postValidationService;

    @Transactional(readOnly = true)
    public IPaginationResponse<UserSummaryResponse> execute(UUID postId, int page) {
        postValidationService.findActivePost(postId);
        var pageable = PageRequest.of(Math.max(page, 0), 20, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        var likes = likeRepository.findByPost_IdAndDeletedAtIsNullAndUser_DeletedAtIsNull(postId, pageable);
        return IPaginationResponse.from(likes.map(like -> UserSummaryResponse.from(like.getUser())));
    }
}
