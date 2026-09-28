package com.antony.openjobs.modules.posts.usecase.create;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Size;

public record CreatePostRequest(
        @Size (max = 1000)
        String content,

        MultipartFile file
) {}
