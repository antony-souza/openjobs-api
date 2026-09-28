package com.antony.openjobs.modules.posts.usecase.create;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.antony.openjobs.config.upload.IFileUploadService;
import com.antony.openjobs.modules.posts.model.PostEntity;
import com.antony.openjobs.modules.posts.repository.IPostRepository;
import com.antony.openjobs.modules.users.repository.IUserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreatePostUseCase {
    private final IFileUploadService fileUploadService;
    private final IPostRepository postRepository;
    private final IUserRepository userRepository;

    @Transactional
    public CreatePostResponse execute(CreatePostRequest request, UUID userId) {
        var content = request.content() == null ? "" : request.content().trim();

        if (content.isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, 
                "O conteúdo do post é obrigatório"
            );
        }

        var user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, 
                    "Usuário não encontrado")
                );

        var fileUrl = request.file() == null || request.file().isEmpty()
                ? null
                : fileUploadService.upload(request.file(), "posts");

        var post = new PostEntity();
        
        post.setContent(content);
        post.setFileUrl(fileUrl);
        post.setUser(user);

        postRepository.save(post);

        return new CreatePostResponse("Publicação feita com sucesso");
    }
}
