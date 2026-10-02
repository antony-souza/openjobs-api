package com.antony.openjobs.modules.posts.services;

import com.antony.openjobs.modules.posts.model.PostEntity;
import com.antony.openjobs.modules.posts.repository.IPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostValidationService {
    private final IPostRepository postRepository;

    public PostEntity findActivePost(UUID postId) {
        return postRepository.findByIdAndDeletedAtIsNull(postId)
                .filter(post -> post.getUser().getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Publicação não encontrada"));
    }
}
