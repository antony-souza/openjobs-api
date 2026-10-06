package com.antony.openjobs.modules.jobs.usecase.capabilities;

import com.antony.openjobs.modules.permissions.model.Permission;
import com.antony.openjobs.modules.rolepermissions.repository.IRolePermissionRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindJobCapabilitiesUseCaseTest {
    @Test
    void derivesPublishAccessFromTheExistingRolePermission() {
        var repository = mock(IRolePermissionRepository.class);
        var useCase = new FindJobCapabilitiesUseCase(repository);
        var roleId = UUID.randomUUID();
        assertThat(useCase.execute(roleId).canPublish()).isFalse();
        when(repository.existsByRole_IdAndPermission_CodeAndDeletedAtIsNullAndRole_DeletedAtIsNullAndPermission_DeletedAtIsNull(
                roleId, Permission.JOB_CREATE.getCode())).thenReturn(true);
        assertThat(useCase.execute(roleId).canPublish()).isTrue();
        assertThat(useCase.execute(roleId).canManage()).isTrue();
        assertThat(useCase.execute(roleId).canEdit()).isFalse();
    }

    @Test
    void allowsManagementForEditorsWithoutGrantingPublishAccess() {
        var repository = mock(IRolePermissionRepository.class);
        var roleId = UUID.randomUUID();
        when(repository.existsByRole_IdAndPermission_CodeAndDeletedAtIsNullAndRole_DeletedAtIsNullAndPermission_DeletedAtIsNull(
                roleId, Permission.JOB_UPDATE.getCode())).thenReturn(true);
        var capabilities = new FindJobCapabilitiesUseCase(repository).execute(roleId);
        assertThat(capabilities.canManage()).isTrue();
        assertThat(capabilities.canEdit()).isTrue();
        assertThat(capabilities.canPublish()).isFalse();
    }
}
