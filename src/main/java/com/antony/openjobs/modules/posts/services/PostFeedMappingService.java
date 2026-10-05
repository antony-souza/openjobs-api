package com.antony.openjobs.modules.posts.services;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.comments.repository.ICommentRepository;
import com.antony.openjobs.modules.likes.repository.ILikeRepository;
import com.antony.openjobs.modules.posts.model.PostEntity;
import com.antony.openjobs.modules.posts.repository.projection.PostCount;
import com.antony.openjobs.modules.posts.usecase.feed.FeedResponse;
import com.antony.openjobs.modules.users.usecase.UserSummaryResponse;
import com.antony.openjobs.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostFeedMappingService {
    private final ILikeRepository likeRepository;
    private final ICommentRepository commentRepository;

    public IPaginationResponse<FeedResponse> map(Page<PostEntity> posts, UUID viewerId) {
        var ids = posts.stream().map(PostEntity::getId).toList();

        if (ids.isEmpty()) {
            return new IPaginationResponse<>(posts.getNumber(), posts.getSize(), posts.getTotalElements(), List.of());
        }

        var likes = counts(likeRepository.countForPosts(ids));
        var comments = counts(commentRepository.countForPosts(ids));
        Set<UUID> liked = viewerId == null ? Set.of() : likeRepository.findByPost_IdInAndUser_IdAndDeletedAtIsNull(ids, viewerId)
                .stream().map(like -> like.getPost().getId()).collect(Collectors.toSet());

        var response = posts.map(post -> new FeedResponse(
                post.getId(),
                post.getContent(),
                post.getFileUrl(),
                DateTimeUtils.withServerOffset(post.getCreatedAt()),
                UserSummaryResponse.from(post.getUser()),
                likes.getOrDefault(post.getId(), 0L),
                comments.getOrDefault(post.getId(), 0L),
                liked.contains(post.getId())
        ));

        return IPaginationResponse.from(response);
    }

    private Map<UUID, Long> counts(List<PostCount> counts) {
        return counts.stream().collect(Collectors.toMap(PostCount::getPostId, PostCount::getTotal));
    }
}
