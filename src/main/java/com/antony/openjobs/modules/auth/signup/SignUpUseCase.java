package com.antony.openjobs.modules.auth.signup;

import lombok.RequiredArgsConstructor;
import com.antony.openjobs.config.security.TokenProvider;
import com.antony.openjobs.services.queue.QueueService;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.UserRepository;
import com.antony.openjobs.services.email.EmailMessage;
import com.antony.openjobs.utils.QueueNameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SignUpUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final QueueService queueService;

    public SignUpResponse execute(SignUpRequest request) {

        if (userRepository.existsByEmailAndUsernameAndDeletedAtIsNull(
                request.email().trim(),
                request.username().trim())
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este email já está cadastrado"
            );
        }

        UserEntity user = new UserEntity();
        user.setName(request.name().trim());
        user.setEmail(request.email().trim());
        user.setUsername(request.username().trim());
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
        String token = tokenProvider.generateToken(createdUser.getId().toString());

        return new SignUpResponse("Conta criada com sucesso", token);
    }

}
