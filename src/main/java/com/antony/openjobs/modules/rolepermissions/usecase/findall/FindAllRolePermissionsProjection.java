package com.antony.openjobs.modules.rolepermissions.usecase.findall;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "roleId", "roleName", "permissionId", "permissionName", "permissionCode" })
public interface FindAllRolePermissionsProjection {
    UUID getId();

    UUID getRoleId();

    String getRoleName();

    UUID getPermissionId();

    String getPermissionName();

    String getPermissionCode();
}
