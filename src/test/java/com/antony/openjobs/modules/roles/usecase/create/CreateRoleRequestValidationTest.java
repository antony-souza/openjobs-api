package com.antony.openjobs.modules.roles.usecase.create;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateRoleRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldAcceptValidRoleRequest() {
        var violations = validator.validate(
                new CreateRoleRequest("Fundador", "founder", 100)
        );

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldReturnEveryConstraintViolation() {
        var violations = validator.validate(
                new CreateRoleRequest("", "", -1)
        );

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrder("name", "code", "level");
    }
}
