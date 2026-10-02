package com.antony.openjobs.modules.posts.usecase.update;

import com.antony.openjobs.utils.PostContentUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record UpdatePostRequest(
        @NotBlank(message = "O conteúdo do post é obrigatório")
        @Size(max = PostContentUtils.MAX_LENGTH, message = "A publicação deve ter no máximo 3000 caracteres")
        String content,
        MultipartFile file,
        boolean removeFile
) {}
