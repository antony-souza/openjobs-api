package com.antony.openjobs.modules.permissions.usecase.update;

import com.antony.openjobs.modules.permissions.model.PermissionEntity;
import com.antony.openjobs.modules.permissions.repository.PermissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatePermissionUseCaseTest {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private UpdatePermissionUseCase useCase;

    @Test
    void updatesAnActivePermission() {
        UUID id = UUID.randomUUID();
        var permission = new PermissionEntity();
        var request = new UpdatePermissionRequest(
                "Criar vagas", "JOB_CREATE", "Permite publicar uma vaga."
        );
        when(permissionRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.of(permission));

        var response = useCase.execute(id, request);

        assertThat(response.message()).isEqualTo("Permission updated successfully");
        assertThat(permission.getName()).isEqualTo("Criar vagas");
        assertThat(permission.getCode()).isEqualTo("JOB_CREATE");
        assertThat(permission.getDescription()).isEqualTo("Permite publicar uma vaga.");
        verify(permissionRepository).save(permission);
    }

    @Test
    void rejectsAnUnknownPermission() {
        UUID id = UUID.randomUUID();
        when(permissionRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id,
                new UpdatePermissionRequest("Criar vagas", "JOB_CREATE", "Descrição")))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception -> {
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(exception.getReason()).isEqualTo("Permission not found");
                });

        verify(permissionRepository, never()).save(any());
    }
}
