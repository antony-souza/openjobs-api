package com.antony.openjobs.modules.roles.usecase.create;

import com.antony.openjobs.modules.roles.model.RoleEntity;
import com.antony.openjobs.modules.roles.repository.RoleRepository;
import com.antony.openjobs.modules.roles.services.RoleValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateRoleUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleValidationService roleValidationService;

    @InjectMocks
    private CreateRoleUseCase createRoleUseCase;

    @Captor
    private ArgumentCaptor<RoleEntity> roleCaptor;

    @Test
    void shouldCreateRoleWhenCodeIsAvailable() {
        CreateRoleRequest request = new CreateRoleRequest("Fundador", "founder", 100);

        CreateRoleResponse response = createRoleUseCase.execute(request);

        assertThat(response.message()).isEqualTo("Role cadastrada com sucesso!");
        verify(roleValidationService).validateDuplicateRole("founder", null);
        verify(roleRepository).save(roleCaptor.capture());

        RoleEntity role = roleCaptor.getValue();
        assertThat(role.getName()).isEqualTo("Fundador");
        assertThat(role.getCode()).isEqualTo("founder");
        assertThat(role.getLevel()).isEqualTo(100);
    }

    @Test
    void shouldRejectRoleWhenCodeAlreadyExists() {
        CreateRoleRequest request = new CreateRoleRequest("Fundador", "founder", 100);
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma role com este código"))
                .when(roleValidationService)
                .validateDuplicateRole("founder", null);

        assertThatThrownBy(() -> createRoleUseCase.execute(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(responseException.getReason()).isEqualTo("Já existe uma role com este código");
                });

        verify(roleRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
