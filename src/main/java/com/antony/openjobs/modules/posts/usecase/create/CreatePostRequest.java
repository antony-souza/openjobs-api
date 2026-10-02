package com.antony.openjobs.modules.posts.usecase.create;

import com.antony.openjobs.utils.PostContentUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Size;

public record CreatePostRequest(
        @Size(max = PostContentUtils.MAX_LENGTH, message = "A publicação deve ter no máximo 3000 caracteres")
        String content,

        MultipartFile file
) {}
