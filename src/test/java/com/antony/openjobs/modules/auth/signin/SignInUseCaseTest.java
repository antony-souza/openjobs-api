package com.antony.openjobs.modules.auth.signin;

import com.antony.openjobs.config.security.TokenProvider;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignInUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private SignInUseCase signInUseCase;

    @Test
    void shouldAuthenticateUserWithValidCredentials() {
        var request = new SignInRequest("antony@example.com", "password123");
        var userId = UUID.randomUUID();
        var user = new UserEntity();
        user.setId(userId);
        user.setEmail("antony@example.com");
        user.setPassword("encoded-password");

        when(userRepository.findByEmail("antony@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);
        when(tokenProvider.generateToken(userId.toString())).thenReturn("jwt-token");

        var response = signInUseCase.execute(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        verify(passwordEncoder).matches("password123", "encoded-password");
        verify(tokenProvider).generateToken(userId.toString());
    }

    @Test
    void shouldRejectAuthenticationWhenEmailDoesNotExist() {
        var request = new SignInRequest("missing@example.com", "password123");
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> signInUseCase.execute(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email ou senha inválidos");

        verifyNoInteractions(passwordEncoder, tokenProvider);
    }

    @Test
    void shouldRejectAuthenticationWhenPasswordDoesNotMatch() {
        var request = new SignInRequest("antony@example.com", "wrong-password");
        var user = new UserEntity();
        user.setPassword("encoded-password");

        when(userRepository.findByEmail("antony@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> signInUseCase.execute(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email ou senha inválidos");

        verify(tokenProvider, never()).generateToken(org.mockito.ArgumentMatchers.anyString());
    }
}
