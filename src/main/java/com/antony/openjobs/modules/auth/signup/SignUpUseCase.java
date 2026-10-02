package com.antony.openjobs.modules.auth.signup;

import lombok.RequiredArgsConstructor;
import com.antony.openjobs.config.security.TokenProvider;
import com.antony.openjobs.modules.roles.model.RoleEntity;
import com.antony.openjobs.modules.roles.repository.IRoleRepository;
import com.antony.openjobs.services.queue.QueueService;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.IUserRepository;
import com.antony.openjobs.services.email.EmailMessage;
import com.antony.openjobs.utils.QueueNameUtils;
import com.antony.openjobs.utils.RoleCodeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SignUpUseCase {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final QueueService queueService;

    public SignUpResponse execute(SignUpRequest request) {
        String email = request.email().trim().toLowerCase();
        String username = request.username().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmailAndDeletedAtIsNull(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este email já está cadastrado"
            );
        }

        if (userRepository.existsByUsernameIgnoreCaseAndDeletedAtIsNull(username)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este username já está cadastrado"
            );
        }

        RoleEntity role = roleRepository.findByCodeAndDeletedAtIsNull(RoleCodeUtils.CANDIDATE)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "Cadastro indisponível no momento"
                ));

        UserEntity user = new UserEntity();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setUsername(username);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(request.password()));

        UserEntity createdUser = userRepository.save(user);

        queueService.addInQueue(
                QueueNameUtils.GENERIC_EMAILS,
                new EmailMessage(
                        user.getEmail(),
                        "Bem-vindo à OpenJobs!",
                        "Sua conta foi criada com sucesso."
                )
        );
        String token = tokenProvider.generateToken(createdUser.getId(), role.getId());

        return new SignUpResponse("Conta criada com sucesso", token);
    }

}
