package com.antony.openjobs.modules.likes.usecase.delete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.antony.openjobs.modules.likes.model.LikeEntity;
import com.antony.openjobs.modules.likes.repository.ILikeRepository;

@ExtendWith(MockitoExtension.class)
class DeleteLikeUseCaseTest {

    @Mock private ILikeRepository likeRepository;
    @InjectMocks private DeleteLikeUseCase deleteLikeUseCase;

    @Test
    void shouldSetDeletedAtOnLike() {
        var userId = UUID.randomUUID();
        var postId = UUID.randomUUID();
        var like = new LikeEntity();
        when(likeRepository.findByUser_IdAndPost_Id(userId, postId)).thenReturn(Optional.of(like));

        var response = deleteLikeUseCase.execute(postId, userId);

        assertThat(response.message()).isEqualTo("Like deleted successfully for post");
        assertThat(like.getDeletedAt()).isNotNull();
    }
}
