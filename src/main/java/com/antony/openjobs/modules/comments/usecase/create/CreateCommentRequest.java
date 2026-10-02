package com.antony.openjobs.modules.comments.usecase.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateCommentRequest(
        @NotBlank(message = "Escreva um comentário")
        @Size(max = 1000, message = "O comentário deve ter no máximo 1000 caracteres") String content,
        UUID parentCommentId
) {
    public CreateCommentRequest(String content) {
        this(content, null);
    }
}
