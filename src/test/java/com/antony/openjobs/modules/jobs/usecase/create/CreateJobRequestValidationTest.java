package com.antony.openjobs.modules.jobs.usecase.create;

import com.antony.openjobs.modules.jobs.usecase.update.UpdateJobRequest;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateJobRequestValidationTest {
    @Test
    void validatesTheSameDescriptionLimitWhenUpdatingAJob() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            assertThat(validator.validate(new UpdateJobRequest("Vaga", "a".repeat(3000)))).isEmpty();
            assertThat(validator.validate(new UpdateJobRequest("Vaga", "a".repeat(3001))))
                    .extracting(violation -> violation.getPropertyPath().toString())
                    .containsExactly("description");
        }
    }

    @Test
    void rejectsBlankAndOversizedFieldsBeforePersistingToTheDatabase() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            assertThat(validator.validate(new CreateJobRequest("a".repeat(255), "a".repeat(3000)))).isEmpty();
            assertThat(validator.validate(new CreateJobRequest("a".repeat(256), "a".repeat(3001))))
                    .extracting(violation -> violation.getPropertyPath().toString())
                    .containsExactlyInAnyOrder("title", "description");
            assertThat(validator.validate(new CreateJobRequest(" ", "\n")))
                    .extracting(violation -> violation.getPropertyPath().toString())
                    .containsExactlyInAnyOrder("title", "description");
        }
    }
}
