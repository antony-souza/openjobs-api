package com.antony.openjobs.config.scripts;

import com.antony.openjobs.modules.roles.model.RoleEntity;
import com.antony.openjobs.modules.roles.repository.RoleRepository;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class DefaultUserSeed implements ApplicationRunner {
    private static final String ADMIN_ROLE_NAME = "Administrador";
    private static final String ADMIN_ROLE_CODE = "admin";
    private static final int ADMIN_ROLE_LEVEL = 100;
    private static final String ADMIN_NAME = "Administrador";
    private static final String ADMIN_EMAIL = "admin@openjobs.com.br";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "password";

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
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
}
