package com.antony.openjobs.modules.likes.usecase.update;

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
public class UpdateLikeUseCase {

    private final ILikeRepository likeRepository;

    @Transactional
    public LikeResponse execute(UUID postId, UUID userId) {
        var like = likeRepository.findByUser_IdAndPost_Id(userId, postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Like não encontrado para este post"));

        if (like.getDeletedAt() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Like já está ativo para este post");
        }

        like.setDeletedAt(null);

        return new LikeResponse("Like ativado com sucesso para o post");
    }
}
