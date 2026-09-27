package com.antony.openjobs.modules.permissions.usecase.findall;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "name", "code", "description" })
public interface FindAllPermissionsProjection {
    UUID getId();

    String getName();

    String getCode();

    String getDescription();
}
