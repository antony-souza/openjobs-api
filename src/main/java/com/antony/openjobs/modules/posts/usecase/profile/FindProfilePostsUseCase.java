package com.antony.openjobs.modules.posts.usecase.profile;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.posts.repository.IPostRepository;
import com.antony.openjobs.modules.posts.services.PostFeedMappingService;
import com.antony.openjobs.modules.posts.usecase.feed.FeedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindProfilePostsUseCase {
    private final IPostRepository postRepository;
    private final PostFeedMappingService postFeedMappingService;

    @Transactional(readOnly = true)
    public IPaginationResponse<FeedResponse> execute(UUID userId, int page, int size) {
        var pageable = PageRequest.of(
                Math.max(page, 0), Math.min(Math.max(size, 1), 30),
                Sort.by(Sort.Direction.DESC, "createdAt", "id")
        );
        var posts = postRepository.findByUser_IdAndDeletedAtIsNullAndUser_DeletedAtIsNull(userId, pageable);
        return postFeedMappingService.map(posts, userId);
    }
}
