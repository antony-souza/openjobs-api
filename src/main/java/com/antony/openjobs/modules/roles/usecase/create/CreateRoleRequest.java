package com.antony.openjobs.modules.roles.usecase.create;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRoleRequest(
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @NotBlank(message = "O código é obrigatório")
        String code,

        @NotNull(message = "O level é obrigatório")
        @Min(value = 0, message = "O level deve ser no mínimo 0")
        Integer level
) {
}
