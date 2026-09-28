package com.antony.openjobs.modules.likes.usecase.create;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.antony.openjobs.modules.likes.model.LikeEntity;
import com.antony.openjobs.modules.likes.repository.ILikeRepository;
import com.antony.openjobs.modules.likes.usecase.LikeResponse;
import com.antony.openjobs.modules.posts.repository.IPostRepository;
import com.antony.openjobs.modules.users.repository.IUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateLikeUseCase {

    private final ILikeRepository likeRepository;
    private final IPostRepository postRepository;
    private final IUserRepository userRepository;

    @Transactional
    public LikeResponse execute(UUID postId, UUID userId) {
        if (likeRepository.findByUser_IdAndPost_Id(userId, postId).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Like já existe para este post");
        }

        var user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado"));
        var post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Post não encontrado"));

        var like = new LikeEntity();

        like.setUser(user);
        like.setPost(post);

        likeRepository.save(like);

        return new LikeResponse("Like created successfully for post");
    }
}
