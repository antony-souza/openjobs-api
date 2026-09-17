package com.antony.openjobs.modules.users.usecase.findall;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FindAllUsersProjectionSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldExposeRoleNameAsRoleInJson() throws Exception {
        FindAllUsersProjection projection = new FindAllUsersProjection() {
            @Override
            public UUID getId() {
                return UUID.fromString("d0b5686e-f51b-4ac9-b944-e73312ae962b");
            }

            @Override
            public String getName() {
                return "Antony Souza";
            }

            @Override
            public String getEmail() {
                return "antony@infojobs.com.br";
            }

            @Override
            public String getRoleName() {
                return "Administrador";
            }
        };

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(projection));

        assertThat(json.fieldNames()).toIterable()
                .containsExactly("id", "name", "email", "role");
        assertThat(json.get("role").asText()).isEqualTo("Administrador");
        assertThat(json.has("roleName")).isFalse();
    }
}
