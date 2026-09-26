package com.antony.openjobs.config.security;

import com.antony.openjobs.modules.rolepermissions.repository.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class RequiresPermissionAuthorizationManager implements AuthorizationManager<MethodInvocation> {

    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public AuthorizationDecision authorize(
            Supplier<? extends Authentication> authenticationSupplier,
            MethodInvocation invocation
    ) {
        Authentication authentication = authenticationSupplier.get();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof AuthenticatedUser authenticatedUser)) {
            return new AuthorizationDecision(false);
        }

        RequiresPermission requiredPermission = invocation.getMethod().getAnnotation(RequiresPermission.class);
        if (requiredPermission == null) {
            return new AuthorizationDecision(false);
        }

        boolean permitted = rolePermissionRepository
                .existsByRole_IdAndPermission_CodeAndDeletedAtIsNullAndRole_DeletedAtIsNullAndPermission_DeletedAtIsNull(
                        authenticatedUser.roleId(),
                        requiredPermission.value().getCode()
        );
        return new AuthorizationDecision(permitted);
    }
}
