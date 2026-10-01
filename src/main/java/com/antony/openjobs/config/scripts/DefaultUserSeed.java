package com.antony.openjobs.config.scripts;

import com.antony.openjobs.modules.permissions.model.PermissionEntity;
import com.antony.openjobs.modules.permissions.repository.IPermissionRepository;
import com.antony.openjobs.modules.rolepermissions.model.RolePermissionEntity;
import com.antony.openjobs.modules.rolepermissions.repository.IRolePermissionRepository;
import com.antony.openjobs.modules.roles.model.RoleEntity;
import com.antony.openjobs.modules.roles.repository.IRoleRepository;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

// Para rodar a seed, descomente a anotação @Component e execute a aplicação.
// @Component
@RequiredArgsConstructor
@Order(2)
public class DefaultUserSeed implements ApplicationRunner {
    private static final String ADMIN_ROLE_NAME = "Administrador";
    static final String ADMIN_ROLE_CODE = "admin";
    private static final int ADMIN_ROLE_LEVEL = 999;
    private static final String ADMIN_NAME = "Administrador";
    private static final String ADMIN_EMAIL = "admin@openjobs.com.br";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "password";

    private final IRoleRepository roleRepository;
    private final IUserRepository userRepository;
    private final IPermissionRepository permissionRepository;
    private final IRolePermissionRepository rolePermissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        RoleEntity adminRole = roleRepository.findByCode(ADMIN_ROLE_CODE)
                .orElseGet(this::createAdministratorRole);

        UserEntity adminUser = userRepository.findByEmail(ADMIN_EMAIL)
                .orElseGet(this::createAdministratorUser);

        adminUser.setRole(adminRole);
        userRepository.save(adminUser);

        linkAllPermissionsToRole(adminRole);
    }

    private RoleEntity createAdministratorRole() {
        RoleEntity role = new RoleEntity();
        role.setName(ADMIN_ROLE_NAME);
        role.setCode(ADMIN_ROLE_CODE);
        role.setLevel(ADMIN_ROLE_LEVEL);
        return roleRepository.save(role);
    }

    private UserEntity createAdministratorUser() {
        UserEntity user = new UserEntity();
        user.setName(ADMIN_NAME);
        user.setEmail(ADMIN_EMAIL);
        user.setUsername(ADMIN_USERNAME);
        user.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        return user;
    }

    private void linkAllPermissionsToRole(RoleEntity role) {
        for (PermissionEntity permission : permissionRepository.findAllByDeletedAtIsNull()) {
            var existingLink = rolePermissionRepository.findByRoleIdAndPermissionId(
                    role.getId(),
                    permission.getId());

            if (existingLink.isPresent()) {
                RolePermissionEntity link = existingLink.get();
                if (link.getDeletedAt() != null) {
                    link.setDeletedAt(null);
                    rolePermissionRepository.save(link);
                }
                continue;
            }

            RolePermissionEntity link = new RolePermissionEntity();
            link.setRole(role);
            link.setPermission(permission);
            rolePermissionRepository.save(link);
        }
    }
}
