package com.antony.openjobs.modules.likes.usecase.upsert;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.antony.openjobs.modules.likes.model.LikeEntity;
import com.antony.openjobs.modules.likes.repository.ILikeRepository;
import com.antony.openjobs.modules.posts.repository.IPostRepository;
import com.antony.openjobs.modules.users.repository.IUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpsertLikeUseCase {
    private final ILikeRepository likeRepository;
    private final IPostRepository postRepository;
    private final IUserRepository userRepository;

    @Transactional
    public UpsertLikeResponse execute(UUID postId, UUID userId) {

        var user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado"));

        var post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Post não encontrado"));

        return likeRepository.findByUser_IdAndPost_Id(userId, postId)
                .map(like -> {
                    var wasRemoved = like.getDeletedAt() != null;

                    like.setDeletedAt(wasRemoved ? null : LocalDateTime.now());

                    return new UpsertLikeResponse(
                            wasRemoved
                                    ? "Like created successfully for post"
                                    : "Like removed successfully for post");
                })
                .orElseGet(() -> {
                    var likeEntity = new LikeEntity();

                    likeEntity.setUser(user);
                    likeEntity.setPost(post);

                    likeRepository.save(likeEntity);

                    return new UpsertLikeResponse("Like created successfully for post");
                });
    }
}
