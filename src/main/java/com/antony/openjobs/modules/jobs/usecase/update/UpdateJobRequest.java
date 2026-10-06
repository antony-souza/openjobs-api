package com.antony.openjobs.modules.jobs.usecase.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateJobRequest(
        @NotBlank(message = "O título é obrigatório")
        String title,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 3000, message = "A descrição deve ter no máximo 3000 caracteres")
        String description
) {
}
