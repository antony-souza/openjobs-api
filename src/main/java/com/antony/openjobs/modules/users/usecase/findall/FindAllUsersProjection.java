package com.antony.openjobs.modules.users.usecase.findall;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.UUID;

@JsonPropertyOrder({"id", "name", "email", "role"})
public interface FindAllUsersProjection {
    UUID getId();

    String getName();

    String getEmail();

    @JsonProperty("role")
    String getRoleName();
}
