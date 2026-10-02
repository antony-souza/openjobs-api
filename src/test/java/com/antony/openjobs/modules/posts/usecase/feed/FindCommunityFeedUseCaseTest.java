package com.antony.openjobs.modules.posts.usecase.feed;

import com.antony.openjobs.modules.comments.repository.ICommentRepository;
import com.antony.openjobs.modules.likes.repository.ILikeRepository;
import com.antony.openjobs.modules.likes.model.LikeEntity;
import com.antony.openjobs.modules.posts.repository.projection.PostCount;
import com.antony.openjobs.modules.posts.model.PostEntity;
import com.antony.openjobs.modules.posts.repository.IPostRepository;
import com.antony.openjobs.modules.users.model.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import com.antony.openjobs.modules.posts.services.PostFeedMappingService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindCommunityFeedUseCaseTest {
    @Mock IPostRepository posts;
    @Mock ILikeRepository likes;
    @Mock ICommentRepository comments;
    FindCommunityFeedUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FindCommunityFeedUseCase(posts, new PostFeedMappingService(likes, comments));
    }

    @Test void returnsPostsFromDifferentAuthorsWithCountsAndNewestFirstSort() {
        var viewer = UUID.randomUUID();
        var first = post("Maria"); var second = post("João"); var ids = List.of(first.getId(), second.getId());
        when(posts.findByDeletedAtIsNullAndUser_DeletedAtIsNull(any())).thenReturn(new PageImpl<>(List.of(first, second), PageRequest.of(0, 10), 2));
        when(likes.countForPosts(ids)).thenReturn(List.of(count(first.getId(), 7L)));
        when(comments.countForPosts(ids)).thenReturn(List.of(count(second.getId(), 3L)));
        var like = new LikeEntity();
        like.setPost(first);
        when(likes.findByPost_IdInAndUser_IdAndDeletedAtIsNull(ids, viewer)).thenReturn(List.of(like));
        var response = useCase.execute(viewer, 0, 10);
        assertThat(response.items()).extracting(item -> item.author().name()).containsExactly("Maria", "João");
        assertThat(response.items().get(0).likesCount()).isEqualTo(7); assertThat(response.items().get(0).liked()).isTrue();
        assertThat(response.items().get(1).commentsCount()).isEqualTo(3); assertThat(response.items().get(1).liked()).isFalse();
        var pageable = ArgumentCaptor.forClass(Pageable.class); verify(posts).findByDeletedAtIsNullAndUser_DeletedAtIsNull(pageable.capture());
        assertThat(pageable.getValue().getSort().getOrderFor("createdAt").getDirection()).isEqualTo(Sort.Direction.DESC);
        assertThat(pageable.getValue().getSort().getOrderFor("id").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test void emptyFeedDoesNotRunAggregationQueries() {
        when(posts.findByDeletedAtIsNullAndUser_DeletedAtIsNull(any())).thenReturn(Page.empty(PageRequest.of(0, 10)));
        assertThat(useCase.execute(UUID.randomUUID(), 0, 10).items()).isEmpty(); verifyNoInteractions(likes, comments);
    }

    private PostEntity post(String name) {
        var author = new UserEntity(); author.setId(UUID.randomUUID()); author.setName(name); author.setUsername(name.toLowerCase());
        var post = new PostEntity(); post.setId(UUID.randomUUID()); post.setContent("Olá"); post.setUser(author); return post;
    }
    private PostCount count(UUID id, long total) {
        return new PostCount() { public UUID getPostId() { return id; } public Long getTotal() { return total; } };
    }
}
