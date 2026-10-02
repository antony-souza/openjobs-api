package com.antony.openjobs.modules.posts.usecase.update;

import com.antony.openjobs.config.upload.IFileUploadService;
import com.antony.openjobs.modules.posts.model.PostEntity;
import com.antony.openjobs.modules.posts.repository.IPostRepository;
import com.antony.openjobs.modules.posts.services.PostValidationService;
import com.antony.openjobs.modules.posts.usecase.delete.DeletePostUseCase;
import com.antony.openjobs.modules.users.model.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostOwnershipTest {
    @Mock IPostRepository posts;
    @Mock IFileUploadService uploads;
    UpdatePostUseCase update;
    DeletePostUseCase delete;
    PostEntity post;

    @BeforeEach
    void setUp() {
        var validation = new PostValidationService(posts);
        update = new UpdatePostUseCase(validation, posts, uploads);
        delete = new DeletePostUseCase(validation, posts);
        var author = new UserEntity();
        author.setId(UUID.randomUUID());
        post = new PostEntity();
        post.setId(UUID.randomUUID());
        post.setUser(author);
        post.setContent("Texto original");
    }

    @Test
    void rejectsEditingAndDeletingAnotherAuthorsPostBeforeUploading() {
        when(posts.findByIdAndDeletedAtIsNull(post.getId())).thenReturn(Optional.of(post));
        var stranger = UUID.randomUUID();
        var file = new MockMultipartFile("file", "photo.png", "image/png", new byte[]{1});
        assertThatThrownBy(() -> update.execute(post.getId(), stranger, new UpdatePostRequest("Alterado", file, false)))
                .isInstanceOfSatisfying(ResponseStatusException.class, error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
        assertThatThrownBy(() -> delete.execute(post.getId(), stranger))
                .isInstanceOfSatisfying(ResponseStatusException.class, error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
        assertThat(post.getContent()).isEqualTo("Texto original");
        assertThat(post.getDeletedAt()).isNull();
        verify(posts, never()).save(any());
        verifyNoInteractions(uploads);
    }

    @Test
    void rejectsMissingDeletedPostsAndPostsOfInactiveAuthors() {
        when(posts.findByIdAndDeletedAtIsNull(post.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> delete.execute(post.getId(), post.getUser().getId()))
                .isInstanceOfSatisfying(ResponseStatusException.class, error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        post.getUser().setDeletedAt(LocalDateTime.now());
        when(posts.findByIdAndDeletedAtIsNull(post.getId())).thenReturn(Optional.of(post));
        assertThatThrownBy(() -> update.execute(post.getId(), post.getUser().getId(), new UpdatePostRequest("Alterado", null, false)))
                .isInstanceOfSatisfying(ResponseStatusException.class, error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        verify(posts, never()).save(any());
        verifyNoInteractions(uploads);
    }

    @Test
    void replacesAttachmentOnlyAfterCheckingOwnership() {
        when(posts.findByIdAndDeletedAtIsNull(post.getId())).thenReturn(Optional.of(post));
        var file = new MockMultipartFile("file", "photo.png", "image/png", new byte[]{1});
        when(uploads.upload(file, "posts")).thenReturn("https://example.invalid/new.png");
        update.execute(post.getId(), post.getUser().getId(), new UpdatePostRequest("  Novo texto  ", file, true));
        assertThat(post.getContent()).isEqualTo("Novo texto");
        assertThat(post.getFileUrl()).isEqualTo("https://example.invalid/new.png");
        verify(posts).save(post);
    }
}
