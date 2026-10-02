package com.antony.openjobs.modules.posts.usecase.publicprofile;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.posts.repository.IPostRepository;
import com.antony.openjobs.modules.posts.services.PostFeedMappingService;
import com.antony.openjobs.modules.posts.usecase.feed.FeedResponse;
import com.antony.openjobs.modules.users.services.PublicProfileLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindPublicProfilePostsUseCase {
    private final PublicProfileLookupService publicProfileLookupService;
    private final IPostRepository postRepository;
    private final PostFeedMappingService postFeedMappingService;

    @Transactional(readOnly = true)
    public IPaginationResponse<FeedResponse> execute(String username, UUID viewerId, int page) {
        var user = publicProfileLookupService.findActiveProfile(username);
        var pageable = PageRequest.of(Math.max(page, 0), 10, Sort.by(Sort.Direction.DESC, "createdAt", "id"));
        return postFeedMappingService.map(
                postRepository.findByUser_IdAndDeletedAtIsNullAndUser_DeletedAtIsNull(user.getId(), pageable), viewerId
        );
    }
}
