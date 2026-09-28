package com.antony.openjobs.modules.posts.usecase.findall;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.likes.repository.ILikeRepository;
import com.antony.openjobs.modules.posts.repository.IPostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FindAllPostsByUserIdUseCase {

    private final IPostRepository postRepository;
    private final ILikeRepository likeRepository;

    @Transactional(readOnly = true)
    public IPaginationResponse<FindAllPostsByUserIdResponse> execute(UUID userId, Pageable pageable) {
        var posts = postRepository.findAllByUser_IdAndDeletedAtIsNull(userId, pageable);

        var data = posts.map(post -> {
            var likesCount = likeRepository.countByPost_IdAndDeletedAtIsNull(post.getId());
            return new FindAllPostsByUserIdResponse(
                    post.getId(),
                    post.getContent(),
                    post.getFileUrl(),
                    post.getCreatedAt(),
                    likesCount
            );
        });

        return IPaginationResponse.from(data);
    }
}
