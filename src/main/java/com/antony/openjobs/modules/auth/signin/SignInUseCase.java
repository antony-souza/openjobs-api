package com.antony.openjobs.modules.auth.signin;

import com.antony.openjobs.config.security.TokenProvider;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SignInUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public SignInResponse execute(SignInRequest request) {
        UserEntity user = userRepository
                .findByEmail(request.email())
                .orElseThrow(this::invalidCredentials);

        boolean passwordMatch = passwordEncoder.matches(request.password(), user.getPassword());

        if (!passwordMatch) {
            throw invalidCredentials();
        }

        String token = tokenProvider.generateToken(user.getId().toString());

        return new SignInResponse(token);
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos");
    }
}

