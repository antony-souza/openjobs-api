package com.antony.openjobs.modules.auth.signup;

import lombok.RequiredArgsConstructor;
import com.antony.openjobs.config.security.TokenProvider;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.UserRepository;
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

    public SignUpResponse execute(SignUpRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este email já está cadastrado");
        }

        UserEntity user = new UserEntity();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));

        UserEntity createdUser = userRepository.save(user);
        String token = tokenProvider.generateToken(createdUser.getId().toString());

        return new SignUpResponse("Conta criada com sucesso", token);
    }

}
