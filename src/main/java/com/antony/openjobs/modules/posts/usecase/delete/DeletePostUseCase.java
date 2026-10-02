package com.antony.openjobs.modules.posts.usecase.delete;

import com.antony.openjobs.modules.posts.repository.IPostRepository;
import com.antony.openjobs.modules.posts.services.PostValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletePostUseCase {
    private final PostValidationService postValidationService;
    private final IPostRepository postRepository;

    @Transactional
    public DeletePostResponse execute(UUID postId, UUID userId) {
        var post = postValidationService.findOwnedPost(postId, userId);
        post.setDeletedAt(LocalDateTime.now());
        postRepository.save(post);
        return new DeletePostResponse("Publicação excluída com sucesso");
    }
}
