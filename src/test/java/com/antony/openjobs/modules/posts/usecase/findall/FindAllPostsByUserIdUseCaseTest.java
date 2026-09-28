package com.antony.openjobs.modules.posts.usecase.findall;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.antony.openjobs.modules.posts.repository.IPostRepository;
import com.antony.openjobs.modules.likes.repository.ILikeRepository;

@ExtendWith(MockitoExtension.class)
class FindAllPostsByUserIdUseCaseTest {

    @Mock
    private IPostRepository postRepository;

    @Mock
    private ILikeRepository likeRepository;

    @InjectMocks
    private FindAllPostsByUserIdUseCase findAllPostsByUserIdUseCase;

    @Test
    void shouldReturnOnlyTheAuthenticatedUsersPaginatedPosts() {
        var userId = UUID.randomUUID();
        var postId = UUID.randomUUID();
        var pageable = PageRequest.of(0, 10);
        var post = mock(FindAllPostsByUserIdProjection.class);
        var page = new PageImpl<>(List.of(post), pageable, 11);

        when(post.getId()).thenReturn(postId);
        when(post.getContent()).thenReturn("Meu post");
        when(post.getFileUrl()).thenReturn("https://files.example.test/post.jpeg");
        when(post.getCreatedAt()).thenReturn(LocalDateTime.of(2026, 9, 28, 12, 0));

        when(postRepository.findAllByUser_IdAndDeletedAtIsNull(userId, pageable))
                .thenReturn(page);
        when(likeRepository.countByPost_IdAndDeletedAtIsNull(postId)).thenReturn(3L);

        var response = findAllPostsByUserIdUseCase.execute(userId, pageable);

        assertThat(response.page()).isZero();
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.total()).isEqualTo(11);
        assertThat(response.items()).singleElement()
                .extracting(
                        FindAllPostsByUserIdResponse::id,
                        FindAllPostsByUserIdResponse::content,
                        FindAllPostsByUserIdResponse::likesCount)
                .containsExactly(postId, "Meu post", 3L);
        verify(postRepository).findAllByUser_IdAndDeletedAtIsNull(userId, pageable);
        verify(likeRepository).countByPost_IdAndDeletedAtIsNull(postId);
    }
}
