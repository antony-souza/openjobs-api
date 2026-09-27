package com.antony.openjobs.modules.permissions.usecase.create;

import jakarta.validation.constraints.NotBlank;

public record CreatePermissionRequest(
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "O código é obrigatório")
        String code,

        @NotBlank(message = "O código é obrigatório")
        String description
) {} 
