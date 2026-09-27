package com.antony.openjobs.modules.rolepermissions.usecase.create;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CreateRolePermissionRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void acceptsRoleAndPermissionIds() {
        var request = new CreateRolePermissionRequest(UUID.randomUUID(), UUID.randomUUID());

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void rejectsMissingIds() {
        var request = new CreateRolePermissionRequest(null, null);

        assertThat(validator.validate(request))
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrder("roleId", "permissionId");
    }
}
