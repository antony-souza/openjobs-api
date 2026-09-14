package com.antony.openjobs.modules.jobs.usecase.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CreateJobResponse(
        String message
) {
}
