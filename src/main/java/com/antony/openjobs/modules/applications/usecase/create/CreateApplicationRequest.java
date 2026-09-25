package com.antony.openjobs.modules.applications.usecase.create;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CreateApplicationRequest(
        @NotNull (message = "Job ID cannot be null") 
        UUID jobId
    ) {}
