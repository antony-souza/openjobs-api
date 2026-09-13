package com.antony.openjobs.modules.users.usecase.update;

import com.antony.openjobs.modules.roles.model.RoleEntity;
import com.antony.openjobs.modules.roles.repository.RoleRepository;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCase {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UpdateUserResponse execute(UUID userId, UpdateUserRequest updateUserRequest) {
        UserEntity user = userRepository
                .findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        RoleEntity role = roleRepository
                .findByIdAndDeletedAtIsNull(updateUserRequest.roleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        user.setName(updateUserRequest.name());
        user.setEmail(updateUserRequest.email());
        user.setUsername(updateUserRequest.username());
        user.setRole(role);

        if (updateUserRequest.password() != null && !updateUserRequest.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(updateUserRequest.password()));
        }

        userRepository.save(user);

        return new UpdateUserResponse("Usuário atualizado com sucesso!");
    }
}
