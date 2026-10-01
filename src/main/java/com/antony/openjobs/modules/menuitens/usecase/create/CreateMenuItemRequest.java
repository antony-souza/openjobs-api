package com.antony.openjobs.modules.menuitens.usecase.create;

import jakarta.validation.constraints.NotBlank;

public record CreateMenuItemRequest(
        @NotBlank
        String title,

        @NotBlank
        String iconName,

        @NotBlank
        String path
) {}
