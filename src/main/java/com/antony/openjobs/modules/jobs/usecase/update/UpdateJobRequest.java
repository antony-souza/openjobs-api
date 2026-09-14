package com.antony.openjobs.modules.jobs.usecase.update;

import jakarta.validation.constraints.NotBlank;

public record UpdateJobRequest(
        @NotBlank(message = "O título é obrigatório")
        String title,

        @NotBlank(message = "A descrição é obrigatória")
        String description
) {
}
