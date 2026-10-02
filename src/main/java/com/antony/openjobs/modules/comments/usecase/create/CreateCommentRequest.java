package com.antony.openjobs.modules.comments.usecase.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @NotBlank(message = "Escreva um comentário")
        @Size(max = 1000, message = "O comentário deve ter no máximo 1000 caracteres") String content
) {}
