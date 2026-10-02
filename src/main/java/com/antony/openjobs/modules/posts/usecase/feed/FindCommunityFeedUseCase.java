package com.antony.openjobs.modules.posts.usecase.feed;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.comments.repository.ICommentRepository;
import com.antony.openjobs.modules.likes.repository.ILikeRepository;
import com.antony.openjobs.modules.posts.model.PostEntity;
import com.antony.openjobs.modules.posts.repository.IPostRepository;
import com.antony.openjobs.modules.posts.repository.projection.PostCount;
import com.antony.openjobs.modules.users.usecase.UserSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FindCommunityFeedUseCase {
    private final IPostRepository postRepository;
    private final ILikeRepository likeRepository;
    private final ICommentRepository commentRepository;

    @Transactional(readOnly = true)
    public IPaginationResponse<FeedResponse> execute(UUID viewerId, int page, int size) {
        var pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 30),
                Sort.by(Sort.Direction.DESC, "createdAt", "id")
        );
        var posts = postRepository.findByDeletedAtIsNullAndUser_DeletedAtIsNull(pageable);
        var ids = posts.stream().map(PostEntity::getId).toList();

        if (ids.isEmpty()) {
            return new IPaginationResponse<>(posts.getNumber(), posts.getSize(), posts.getTotalElements(), List.of());
        }

        var likes = counts(likeRepository.countForPosts(ids));
        var comments = counts(commentRepository.countForPosts(ids));
        var liked = likeRepository.findByPost_IdInAndUser_IdAndDeletedAtIsNull(ids, viewerId)
                .stream().map(like -> like.getPost().getId()).collect(Collectors.toSet());

        var response = posts.map(post -> new FeedResponse(
                post.getId(),
                post.getContent(),
                post.getFileUrl(),
                post.getCreatedAt(),
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
