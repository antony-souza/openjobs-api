package com.antony.openjobs.modules.comments.usecase.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCommentRequest(
        @NotBlank(message = "Escreva um comentário")
        @Size(max = 1000, message = "O comentário deve ter no máximo 1000 caracteres")
        String content
) {}
