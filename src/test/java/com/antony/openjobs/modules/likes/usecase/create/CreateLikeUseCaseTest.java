package com.antony.openjobs.modules.likes.usecase.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.antony.openjobs.modules.likes.model.LikeEntity;
import com.antony.openjobs.modules.likes.repository.ILikeRepository;
import com.antony.openjobs.modules.posts.model.PostEntity;
import com.antony.openjobs.modules.posts.repository.IPostRepository;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
class CreateLikeUseCaseTest {

    @Mock private ILikeRepository likeRepository;
    @Mock private IPostRepository postRepository;
    @Mock private IUserRepository userRepository;

    @InjectMocks private CreateLikeUseCase createLikeUseCase;

    @Captor private ArgumentCaptor<LikeEntity> likeCaptor;

    @Test
    void shouldCreateTheFirstLike() {
        var userId = UUID.randomUUID();
        var postId = UUID.randomUUID();
        var user = new UserEntity();
        var post = new PostEntity();
        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(user));
        when(postRepository.findByIdAndDeletedAtIsNull(postId)).thenReturn(Optional.of(post));
        when(likeRepository.findByUser_IdAndPost_Id(userId, postId)).thenReturn(Optional.empty());

        var response = createLikeUseCase.execute(postId, userId);

        assertThat(response.message()).isEqualTo("Like created successfully for post");
        verify(likeRepository).save(likeCaptor.capture());
        assertThat(likeCaptor.getValue().getUser()).isSameAs(user);
        assertThat(likeCaptor.getValue().getPost()).isSameAs(post);
    }

    @Test
    void shouldRejectCreateWhenLikeHistoryAlreadyExists() {
        var userId = UUID.randomUUID();
        var postId = UUID.randomUUID();
        when(likeRepository.findByUser_IdAndPost_Id(userId, postId))
                .thenReturn(Optional.of(new LikeEntity()));

        assertThatThrownBy(() -> createLikeUseCase.execute(postId, userId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode())
                        .isEqualTo(HttpStatus.CONFLICT));
        verify(likeRepository, never()).save(any());
    }
}
