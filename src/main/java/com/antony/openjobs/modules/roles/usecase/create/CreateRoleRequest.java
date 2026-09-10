package com.antony.openjobs.modules.roles.usecase.create;

public record CreateRoleRequest(
        String name,
        String code,
        Integer level
) {
}
