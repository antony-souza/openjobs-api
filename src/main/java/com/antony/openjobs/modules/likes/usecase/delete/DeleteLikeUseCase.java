package com.antony.openjobs.modules.likes.usecase.delete;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.antony.openjobs.modules.likes.repository.ILikeRepository;
import com.antony.openjobs.modules.likes.usecase.LikeResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteLikeUseCase {

    private final ILikeRepository likeRepository;

    @Transactional
    public LikeResponse execute(UUID postId, UUID userId) {
        if (likeRepository.existsByPost_IdAndUser_IdAndDeletedAtIsNotNull(postId, userId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Like já removido para este post");
        }

        var like = likeRepository.findByUser_IdAndPost_Id(userId, postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Like não encontrado para este post"));

        like.setDeletedAt(LocalDateTime.now());

        return new LikeResponse("Like deleted successfully for post");
    }
}
