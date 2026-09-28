package com.antony.openjobs.modules.likes.usecase.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.antony.openjobs.modules.likes.model.LikeEntity;
import com.antony.openjobs.modules.likes.repository.ILikeRepository;

@ExtendWith(MockitoExtension.class)
class UpdateLikeUseCaseTest {

    @Mock private ILikeRepository likeRepository;
    @InjectMocks private UpdateLikeUseCase updateLikeUseCase;

    @Test
    void shouldRestoreLikeByClearingDeletedAt() {
        var userId = UUID.randomUUID();
        var postId = UUID.randomUUID();
        var like = new LikeEntity();
        like.setDeletedAt(LocalDateTime.now().minusDays(1));
        when(likeRepository.findByUser_IdAndPost_Id(userId, postId)).thenReturn(Optional.of(like));

        var response = updateLikeUseCase.execute(postId, userId);

        assertThat(response.message()).isEqualTo("Like updated successfully for post");
        assertThat(like.getDeletedAt()).isNull();
    }

    @Test
    void shouldRejectUpdateWhenLikeIsAlreadyActive() {
        var userId = UUID.randomUUID();
        var postId = UUID.randomUUID();
        var like = new LikeEntity();
        like.setDeletedAt(null);
        when(likeRepository.findByUser_IdAndPost_Id(userId, postId)).thenReturn(Optional.of(like));

        assertThatThrownBy(() -> updateLikeUseCase.execute(postId, userId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode())
                        .isEqualTo(HttpStatus.CONFLICT));
    }
}
