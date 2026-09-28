package com.antony.openjobs.modules.posts.usecase.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.antony.openjobs.config.upload.IFileUploadService;
import com.antony.openjobs.modules.posts.model.PostEntity;
import com.antony.openjobs.modules.posts.repository.IPostRepository;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
class CreatePostUseCaseTest {

    @Mock
    private IFileUploadService fileUploadService;

    @Mock
    private IPostRepository postRepository;

    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private CreatePostUseCase createPostUseCase;

    @Captor
    private ArgumentCaptor<PostEntity> postCaptor;

    @Test
    void shouldCreatePostWithoutFile() {
        var userId = UUID.randomUUID();
        var user = new UserEntity();
        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(user));

        var response = createPostUseCase.execute(
                new CreatePostRequest("  Meu post  ", null),
                userId);

        assertThat(response.message()).isEqualTo("Publicação feita com sucesso");
        verify(postRepository).save(postCaptor.capture());
        assertThat(postCaptor.getValue().getContent()).isEqualTo("Meu post");
        assertThat(postCaptor.getValue().getFileUrl()).isNull();
        assertThat(postCaptor.getValue().getUser()).isSameAs(user);
        verifyNoInteractions(fileUploadService);
    }

    @Test
    void shouldUploadFileAndSaveItsUrlOnPost() {
        var userId = UUID.randomUUID();
        var user = new UserEntity();
        var file = org.mockito.Mockito.mock(MultipartFile.class);
        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.of(user));
        when(file.isEmpty()).thenReturn(false);
        when(fileUploadService.upload(file, "posts"))
                .thenReturn("https://files.example.test/posts/image.jpeg");

        createPostUseCase.execute(new CreatePostRequest("Meu post", file), userId);

        verify(fileUploadService).upload(file, "posts");
        verify(postRepository).save(postCaptor.capture());
        assertThat(postCaptor.getValue().getFileUrl())
                .isEqualTo("https://files.example.test/posts/image.jpeg");
    }

    @Test
    void shouldRejectBlankContentBeforeAccessingDependencies() {
        var request = new CreatePostRequest("   ", null);

        assertThatThrownBy(() -> createPostUseCase.execute(request, UUID.randomUUID()))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    var responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(responseException.getReason())
                            .isEqualTo("O conteúdo do post é obrigatório");
                });

        verifyNoInteractions(userRepository, postRepository, fileUploadService);
    }

    @Test
    void shouldRejectPostWhenUserDoesNotExist() {
        var userId = UUID.randomUUID();
        when(userRepository.findByIdAndDeletedAtIsNull(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createPostUseCase.execute(
                new CreatePostRequest("Meu post", null), userId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    var responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(responseException.getReason()).isEqualTo("Usuário não encontrado");
                });

        verify(postRepository, never()).save(any());
        verifyNoInteractions(fileUploadService);
    }
}
