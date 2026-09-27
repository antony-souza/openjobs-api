package com.antony.openjobs.config.security;

import com.antony.openjobs.modules.permissions.model.Permission;
import com.antony.openjobs.modules.rolepermissions.repository.RolePermissionRepository;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RequiresPermissionAuthorizationManagerTest {

    @Test
    void authorizesWhenTokenRoleHasTheAnnotatedPermission() throws Exception {
        var repository = mock(RolePermissionRepository.class);
        var manager = new RequiresPermissionAuthorizationManager(repository);
        UUID roleId = UUID.randomUUID();
        var authentication = authenticationFor(roleId);
        var invocation = annotatedInvocation();
        when(repository.existsByRole_IdAndPermission_CodeAndDeletedAtIsNullAndRole_DeletedAtIsNullAndPermission_DeletedAtIsNull(
                roleId, "ROLEPERMISSION_CREATE"
        )).thenReturn(true);

        var decision = manager.authorize(() -> authentication, invocation);

        assertThat(decision.isGranted()).isTrue();
        verify(repository)
                .existsByRole_IdAndPermission_CodeAndDeletedAtIsNullAndRole_DeletedAtIsNullAndPermission_DeletedAtIsNull(
                        roleId, "ROLEPERMISSION_CREATE"
                );
    }

    @Test
    void deniesWhenTokenRoleHasNoGrant() throws Exception {
        var repository = mock(RolePermissionRepository.class);
        var manager = new RequiresPermissionAuthorizationManager(repository);
        UUID roleId = UUID.randomUUID();

        var decision = manager.authorize(() -> authenticationFor(roleId), annotatedInvocation());

        assertThat(decision.isGranted()).isFalse();
        verify(repository)
                .existsByRole_IdAndPermission_CodeAndDeletedAtIsNullAndRole_DeletedAtIsNullAndPermission_DeletedAtIsNull(
                        roleId, "ROLEPERMISSION_CREATE"
                );
    }

    @Test
    void deniesAPrincipalWithoutAuthenticatedUser() {
        var repository = mock(RolePermissionRepository.class);
        var manager = new RequiresPermissionAuthorizationManager(repository);
        var authentication = new UsernamePasswordAuthenticationToken("other-principal", null, List.of());

        var decision = manager.authorize(() -> authentication, mock(MethodInvocation.class));

        assertThat(decision.isGranted()).isFalse();
        verifyNoInteractions(repository);
    }

    private static UsernamePasswordAuthenticationToken authenticationFor(UUID roleId) {
        return new UsernamePasswordAuthenticationToken(
                new AuthenticatedUser(UUID.randomUUID(), roleId), null, List.of()
        );
    }

    private static MethodInvocation annotatedInvocation() throws NoSuchMethodException {
        var invocation = mock(MethodInvocation.class);
        when(invocation.getMethod()).thenReturn(ProtectedAction.class.getDeclaredMethod("create"));
        return invocation;
    }

    private static class ProtectedAction {
        @RequiresPermission(Permission.ROLEPERMISSION_CREATE)
        void create() {
        }
    }
}
