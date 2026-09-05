package com.antony.openjobs.modules.auth.signin;

import com.antony.openjobs.config.security.TokenProvider;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public SignInResponse execute(SignInRequest request) {
        UserEntity user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Email ou senha inválidos"));

        boolean passwordMatch = passwordEncoder.matches(request.password(), user.getPassword());

        if (!passwordMatch) {
            throw new RuntimeException("Email ou senha inválidos");
        }

        String token = tokenProvider.generateToken(user.getId().toString());

        return new SignInResponse(token);
    }
}

