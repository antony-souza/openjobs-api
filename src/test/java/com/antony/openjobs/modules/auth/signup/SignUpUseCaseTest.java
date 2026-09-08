package com.antony.openjobs.modules.auth.signup;

import com.antony.openjobs.config.security.TokenProvider;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignUpUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private SignUpUseCase signUpUseCase;

    @Captor
    private ArgumentCaptor<UserEntity> userCaptor;

    @Test
    void shouldCreateAccountWithNormalizedEmailAndEncodedPassword() {
        var request = new SignUpRequest("  Antony Souza  ", "  ANTONY@EXAMPLE.COM  ", "password123");
        var userId = UUID.randomUUID();
        var savedUser = new UserEntity();
        savedUser.setId(userId);

        when(userRepository.findByEmail("antony@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
        when(tokenProvider.generateToken(userId.toString())).thenReturn("jwt-token");

        var response = signUpUseCase.execute(request);

        assertThat(response.message()).isEqualTo("Conta criada com sucesso");
        assertThat(response.token()).isEqualTo("jwt-token");

        verify(userRepository).save(userCaptor.capture());
        var userToSave = userCaptor.getValue();
        assertThat(userToSave.getName()).isEqualTo("Antony Souza");
        assertThat(userToSave.getEmail()).isEqualTo("antony@example.com");
        assertThat(userToSave.getPassword()).isEqualTo("encoded-password");
        verify(tokenProvider).generateToken(userId.toString());
    }

    @Test
    void shouldRejectRegistrationWhenEmailAlreadyExists() {
        var request = new SignUpRequest("Antony Souza", "ANTONY@example.com", "password123");
        when(userRepository.findByEmail("antony@example.com")).thenReturn(Optional.of(new UserEntity()));

        assertThatThrownBy(() -> signUpUseCase.execute(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    var responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(responseException.getReason()).isEqualTo("Este email já está cadastrado");
                });

        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder, tokenProvider);
    }
}
