package com.antony.openjobs.modules.roles.usecase.findall;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.UUID;

@JsonPropertyOrder({"id", "name"})
public interface FindAllRolesProjection {
    UUID getId();

    String getName();
}
