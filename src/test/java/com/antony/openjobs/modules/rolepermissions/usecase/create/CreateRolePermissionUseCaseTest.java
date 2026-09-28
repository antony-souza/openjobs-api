package com.antony.openjobs.modules.rolepermissions.usecase.create;

import com.antony.openjobs.modules.permissions.model.PermissionEntity;
import com.antony.openjobs.modules.permissions.repository.IPermissionRepository;
import com.antony.openjobs.modules.rolepermissions.model.RolePermissionEntity;
import com.antony.openjobs.modules.rolepermissions.repository.IRolePermissionRepository;
import com.antony.openjobs.modules.roles.model.RoleEntity;
import com.antony.openjobs.modules.roles.repository.IRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateRolePermissionUseCaseTest {

    @Mock
    private IRolePermissionRepository rolePermissionRepository;

    @Mock
    private IRoleRepository roleRepository;

    @Mock
    private IPermissionRepository permissionRepository;

    @InjectMocks
    private CreateRolePermissionUseCase useCase;

    @Test
    void linksAnExistingRoleAndPermission() {
        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();
        var role = new RoleEntity();
        var permission = new PermissionEntity();
        when(roleRepository.findByIdAndDeletedAtIsNull(roleId)).thenReturn(Optional.of(role));
        when(permissionRepository.findByIdAndDeletedAtIsNull(permissionId))
                .thenReturn(Optional.of(permission));

        var response = useCase.execute(new CreateRolePermissionRequest(roleId, permissionId));

        assertThat(response.message()).isEqualTo("Role permission created successfully!");
        verify(rolePermissionRepository)
                .existsByRoleIdAndPermissionIdAndDeletedAtIsNull(roleId, permissionId);
        var linkCaptor = ArgumentCaptor.forClass(RolePermissionEntity.class);
        verify(rolePermissionRepository).save(linkCaptor.capture());
        assertThat(linkCaptor.getValue().getRole()).isSameAs(role);
        assertThat(linkCaptor.getValue().getPermission()).isSameAs(permission);
    }

    @Test
    void rejectsAnUnknownRole() {
        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();
        when(roleRepository.findByIdAndDeletedAtIsNull(roleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new CreateRolePermissionRequest(roleId, permissionId)))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception -> {
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(exception.getReason()).isEqualTo("Role not found");
                });

        verifyNoInteractions(permissionRepository, rolePermissionRepository);
    }

    @Test
    void rejectsAnUnknownPermission() {
        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();
        when(roleRepository.findByIdAndDeletedAtIsNull(roleId)).thenReturn(Optional.of(new RoleEntity()));
        when(permissionRepository.findByIdAndDeletedAtIsNull(permissionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new CreateRolePermissionRequest(roleId, permissionId)))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception -> {
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(exception.getReason()).isEqualTo("Permission not found");
                });

        verifyNoInteractions(rolePermissionRepository);
    }

    @Test
    void rejectsAnExistingActiveLink() {
        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();
        when(roleRepository.findByIdAndDeletedAtIsNull(roleId)).thenReturn(Optional.of(new RoleEntity()));
        when(permissionRepository.findByIdAndDeletedAtIsNull(permissionId))
                .thenReturn(Optional.of(new PermissionEntity()));
        when(rolePermissionRepository.existsByRoleIdAndPermissionIdAndDeletedAtIsNull(roleId, permissionId))
                .thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(new CreateRolePermissionRequest(roleId, permissionId)))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception -> {
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(exception.getReason()).isEqualTo("Role permission already exists");
                });

        verify(rolePermissionRepository, never()).save(any());
    }
}
