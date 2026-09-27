package com.antony.openjobs.modules.rolepermissions.usecase.create;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CreateRolePermissionRequest(
    @NotNull (message = "O ID da role é obrigatório")
    UUID roleId,

    @NotNull (message = "O ID da permissão é obrigatório")
    UUID permissionId
) {}
