package com.antony.openjobs.modules.roles.usecase.update;

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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateRoleUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleValidationService roleValidationService;

    @InjectMocks
    private UpdateRoleUseCase updateRoleUseCase;

    @Captor
    private ArgumentCaptor<RoleEntity> roleCaptor;

    @Test
    void shouldUpdateRoleWhenKeepingItsOwnCode() {
        UUID roleId = UUID.randomUUID();
        RoleEntity role = new RoleEntity();
        role.setId(roleId);
        UpdateRoleRequest request = new UpdateRoleRequest("Fundador Geral", "founder", 100);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        UpdateRoleResponse response = updateRoleUseCase.execute(roleId, request);

        assertThat(response.message()).isEqualTo("Role atualizado com sucesso");
        assertThat(role.getName()).isEqualTo("Fundador Geral");
        assertThat(role.getCode()).isEqualTo("founder");
        assertThat(role.getLevel()).isEqualTo(100);
        verify(roleValidationService).validateDuplicateRole("founder", roleId);
        verify(roleRepository).save(roleCaptor.capture());
        assertThat(roleCaptor.getValue()).isSameAs(role);
    }

    @Test
    void shouldReturnNotFoundWhenRoleIdDoesNotExist() {
        UUID roleId = UUID.randomUUID();
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateRoleUseCase.execute(
                roleId,
                new UpdateRoleRequest("Fundador Geral", "founder", 100)
        )).isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(responseException.getReason()).isEqualTo("Role não encontrada");
                });

        verify(roleValidationService, never()).validateDuplicateRole(any(), any());
        verify(roleRepository, never()).save(any());
    }

    @Test
    void shouldNotSaveWhenCodeBelongsToAnotherRole() {
        UUID roleId = UUID.randomUUID();
        RoleEntity role = new RoleEntity();
        UpdateRoleRequest request = new UpdateRoleRequest("Administrador", "admin", 100);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma role com este código"))
                .when(roleValidationService)
                .validateDuplicateRole("admin", roleId);

        assertThatThrownBy(() -> updateRoleUseCase.execute(roleId, request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(responseException.getReason()).isEqualTo("Já existe uma role com este código");
                });

        verify(roleRepository, never()).save(any());
    }
}
