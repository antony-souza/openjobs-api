package com.antony.openjobs.modules.permissions.usecase.create;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreatePermissionRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void acceptsACompletePermission() {
        var request = new CreatePermissionRequest("Criar vagas", "JOB_CREATE", "Permite publicar vagas.");

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void rejectsBlankFields() {
        var request = new CreatePermissionRequest(" ", " ", " ");

        assertThat(validator.validate(request))
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrder("name", "code", "description");
    }
}
