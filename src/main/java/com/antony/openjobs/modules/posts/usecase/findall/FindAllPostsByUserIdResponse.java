package com.antony.openjobs.modules.posts.usecase.findall;

import java.time.LocalDateTime;
import java.util.UUID;

public record FindAllPostsByUserIdResponse(
        UUID id,
        String content,
        String fileUrl,
        LocalDateTime createdAt,
        Long likesCount
) {}
