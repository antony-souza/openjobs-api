package com.antony.openjobs.modules.likes.usecase.set;

import com.antony.openjobs.modules.likes.model.LikeEntity;
import com.antony.openjobs.modules.likes.repository.ILikeRepository;
import com.antony.openjobs.modules.posts.services.PostValidationService;
import com.antony.openjobs.modules.users.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SetPostLikeUseCase {
    private final PostValidationService postValidationService;
    private final ILikeRepository likeRepository;
    private final IUserRepository userRepository;

    @Transactional
    public SetPostLikeResponse execute(UUID postId, UUID userId, SetPostLikeRequest request) {
        var post = postValidationService.findActivePost(postId);
        var existing = likeRepository.findByUser_IdAndPost_Id(userId, postId);

        if (existing.isEmpty() && !request.liked()) {
            return new SetPostLikeResponse(false);
        }

        var like = existing.orElseGet(() -> {
            var created = new LikeEntity();
            var user = userRepository.findByIdAndDeletedAtIsNull(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
            created.setPost(post);
            created.setUser(user);
            return created;
        });
        like.setDeletedAt(request.liked() ? null : LocalDateTime.now());
        likeRepository.save(like);

        return new SetPostLikeResponse(request.liked());
    }
}
