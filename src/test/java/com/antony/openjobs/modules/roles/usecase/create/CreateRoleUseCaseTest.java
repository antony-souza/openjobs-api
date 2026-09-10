package com.antony.openjobs.modules.roles.usecase.create;

import com.antony.openjobs.modules.roles.model.RoleEntity;
import com.antony.openjobs.modules.roles.repository.RoleRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateRoleUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private CreateRoleUseCase createRoleUseCase;

    @Captor
    private ArgumentCaptor<RoleEntity> roleCaptor;

    @Test
    void shouldCreateRoleWhenCodeIsAvailable() {
        CreateRoleRequest request = new CreateRoleRequest("Fundador", "founder", 100);
        when(roleRepository.findByCode("founder")).thenReturn(Optional.empty());

        CreateRoleResponse response = createRoleUseCase.execute(request);

        assertThat(response.message()).isEqualTo("Role cadastrada com sucesso!");
        verify(roleRepository).save(roleCaptor.capture());

        RoleEntity role = roleCaptor.getValue();
        assertThat(role.getName()).isEqualTo("Fundador");
        assertThat(role.getCode()).isEqualTo("founder");
        assertThat(role.getLevel()).isEqualTo(100);
    }

    @Test
    void shouldRejectRoleWhenCodeAlreadyExists() {
        CreateRoleRequest request = new CreateRoleRequest("Fundador", "founder", 100);
        when(roleRepository.findByCode("founder")).thenReturn(Optional.of(new RoleEntity()));

        assertThatThrownBy(() -> createRoleUseCase.execute(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    ResponseStatusException responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(responseException.getReason()).isEqualTo("Role já cadastrada");
                });

        verify(roleRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
