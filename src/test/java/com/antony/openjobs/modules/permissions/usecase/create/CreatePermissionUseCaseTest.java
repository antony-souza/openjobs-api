package com.antony.openjobs.modules.permissions.usecase.create;

import com.antony.openjobs.modules.permissions.model.PermissionEntity;
import com.antony.openjobs.modules.permissions.repository.IPermissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatePermissionUseCaseTest {

    @Mock
    private IPermissionRepository permissionRepository;

    @InjectMocks
    private CreatePermissionUseCase useCase;

    @Test
    void createsPermissionWithTrimmedFields() {
        var request = new CreatePermissionRequest(
                "  Criar vagas  ", "  JOB_CREATE  ", "  Permite publicar vagas.  "
        );

        var response = useCase.execute(request);

        assertThat(response.message()).isEqualTo("Permission created successfully!");
        verify(permissionRepository).existsByCodeAndDeletedAtIsNull("JOB_CREATE");
        var permissionCaptor = ArgumentCaptor.forClass(PermissionEntity.class);
        verify(permissionRepository).save(permissionCaptor.capture());
        assertThat(permissionCaptor.getValue().getName()).isEqualTo("Criar vagas");
        assertThat(permissionCaptor.getValue().getCode()).isEqualTo("JOB_CREATE");
        assertThat(permissionCaptor.getValue().getDescription()).isEqualTo("Permite publicar vagas.");
    }

    @Test
    void rejectsAnExistingActiveCode() {
        var request = new CreatePermissionRequest("Criar vagas", "JOB_CREATE", "Permite publicar vagas.");
        when(permissionRepository.existsByCodeAndDeletedAtIsNull("JOB_CREATE")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception -> {
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(exception.getReason()).isEqualTo("Permission with code already exists");
                });

        verify(permissionRepository, never()).save(any());
    }
}
