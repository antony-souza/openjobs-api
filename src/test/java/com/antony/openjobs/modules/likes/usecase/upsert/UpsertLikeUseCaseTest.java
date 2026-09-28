package com.antony.openjobs.modules.likes.usecase.upsert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.antony.openjobs.modules.likes.model.LikeEntity;
import com.antony.openjobs.modules.likes.repository.ILikeRepository;
import com.antony.openjobs.modules.posts.model.PostEntity;
import com.antony.openjobs.modules.posts.repository.IPostRepository;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
class UpsertLikeUseCaseTest {

    @Mock
    private ILikeRepository likeRepository;

    @Mock
    private IPostRepository postRepository;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private UpsertLikeUseCase upsertLikeUseCase;

    @Captor
    private ArgumentCaptor<LikeEntity> likeCaptor;

    @Test
    void shouldCreateLikeWhenThereIsNoLikeHistory() {
        var userId = UUID.randomUUID();
        var postId = UUID.randomUUID();
        var user = new UserEntity();
        var post = new PostEntity();

        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(user));
        when(postRepository.findByIdAndDeletedAtIsNull(postId)).thenReturn(Optional.of(post));
        when(likeRepository.findByUser_IdAndPost_Id(userId, postId)).thenReturn(Optional.empty());

        var response = upsertLikeUseCase.execute(postId, userId);

        assertThat(response.message()).isEqualTo("Like created successfully for post");
        verify(likeRepository).save(likeCaptor.capture());
        assertThat(likeCaptor.getValue().getUser()).isSameAs(user);
        assertThat(likeCaptor.getValue().getPost()).isSameAs(post);
    }

    @Test
    void shouldSoftDeleteAnActiveLike() {
        var userId = UUID.randomUUID();
        var postId = UUID.randomUUID();
        var like = like(userId, postId, null);
        mockActiveUserAndPost(userId, postId);
        when(likeRepository.findByUser_IdAndPost_Id(userId, postId)).thenReturn(Optional.of(like));

        var response = upsertLikeUseCase.execute(postId, userId);

        assertThat(response.message()).isEqualTo("Like removed successfully for post");
        assertThat(like.getDeletedAt()).isNotNull();
        verify(likeRepository, never()).save(any());
    }

    @Test
    void shouldRestoreASoftDeletedLike() {
        var userId = UUID.randomUUID();
        var postId = UUID.randomUUID();
        var like = like(userId, postId, LocalDateTime.now().minusDays(1));
        mockActiveUserAndPost(userId, postId);
        when(likeRepository.findByUser_IdAndPost_Id(userId, postId)).thenReturn(Optional.of(like));

        var response = upsertLikeUseCase.execute(postId, userId);

        assertThat(response.message()).isEqualTo("Like created successfully for post");
        assertThat(like.getDeletedAt()).isNull();
        verify(likeRepository, never()).save(any());
    }

    private void mockActiveUserAndPost(UUID userId, UUID postId) {
        when(userRepository.findByIdAndDeletedAtIsNull(userId))
                .thenReturn(Optional.of(new UserEntity()));
        when(postRepository.findByIdAndDeletedAtIsNull(postId))
                .thenReturn(Optional.of(new PostEntity()));
    }

    private LikeEntity like(UUID userId, UUID postId, LocalDateTime deletedAt) {
        var like = new LikeEntity();
        like.setUser(new UserEntity());
        like.setPost(new PostEntity());
        like.setDeletedAt(deletedAt);

        return like;
    }
}
