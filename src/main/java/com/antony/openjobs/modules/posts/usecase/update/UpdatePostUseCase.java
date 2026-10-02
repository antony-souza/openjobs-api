package com.antony.openjobs.modules.posts.usecase.update;

import com.antony.openjobs.config.upload.IFileUploadService;
import com.antony.openjobs.modules.posts.repository.IPostRepository;
import com.antony.openjobs.modules.posts.services.PostValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdatePostUseCase {
    private final PostValidationService postValidationService;
    private final IPostRepository postRepository;
    private final IFileUploadService fileUploadService;

    @Transactional
    public UpdatePostResponse execute(UUID postId, UUID userId, UpdatePostRequest request) {
        var post = postValidationService.findOwnedPost(postId, userId);
        var content = request.content() == null ? "" : request.content().trim();
        if (content.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O conteúdo do post é obrigatório");
        }
        post.setContent(content);
        if (request.file() != null && !request.file().isEmpty()) {
            post.setFileUrl(fileUploadService.upload(request.file(), "posts"));
        } else if (request.removeFile()) {
            post.setFileUrl(null);
        }
        postRepository.save(post);
        return new UpdatePostResponse("Publicação atualizada com sucesso");
    }
}
