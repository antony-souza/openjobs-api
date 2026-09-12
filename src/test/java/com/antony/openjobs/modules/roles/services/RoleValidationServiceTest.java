package com.antony.openjobs.modules.roles.services;

import com.antony.openjobs.modules.roles.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleValidationServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleValidationService roleValidationService;

    @Test
    void shouldAllowUpdatingRoleWithItsOwnCode() {
        UUID roleId = UUID.randomUUID();
        when(roleRepository.existsByCodeAndIdNotAndDeletedAtIsNull("founder", roleId))
                .thenReturn(false);

        assertThatCode(() -> roleValidationService.validateDuplicateRole("founder", roleId))
                .doesNotThrowAnyException();

        verify(roleRepository).existsByCodeAndIdNotAndDeletedAtIsNull("founder", roleId);
    }

    @Test
    void shouldRejectUpdatingRoleWithCodeFromAnotherRole() {
        UUID roleId = UUID.randomUUID();
        when(roleRepository.existsByCodeAndIdNotAndDeletedAtIsNull("admin", roleId))
                .thenReturn(true);

        assertThatThrownBy(() -> roleValidationService.validateDuplicateRole("admin", roleId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    org.assertj.core.api.Assertions.assertThat(responseException.getStatusCode())
                            .isEqualTo(HttpStatus.CONFLICT);
                    org.assertj.core.api.Assertions.assertThat(responseException.getReason())
                            .isEqualTo("Já existe uma role com este código");
                });
    }
}
