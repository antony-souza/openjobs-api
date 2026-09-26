package com.antony.openjobs.modules.permissions.usecase.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePermissionRequest(
        @NotBlank(message = "O nome é obrigatório")
        @NotNull(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "O código é obrigatório")
        @NotNull(message = "O código é obrigatório")
        String code,

        @NotBlank(message = "O código é obrigatório")
        @NotNull(message = "A descrição é obrigatória")
        String description
) {} 
