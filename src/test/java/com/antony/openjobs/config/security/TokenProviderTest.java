package com.antony.openjobs.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TokenProviderTest {

    private static final String JWT_KEY = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    private TokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new TokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtKey", JWT_KEY);
        ReflectionTestUtils.setField(tokenProvider, "jwtExpiration", 60_000L);
    }

    @Test
    void shouldGenerateValidTokenWithUserIdAsSubject() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        var token = tokenProvider.generateToken(userId, roleId);

        assertThat(tokenProvider.isTokenValid(token)).isTrue();
        assertThat(tokenProvider.getSubject(token)).isEqualTo(userId.toString());
    }

    @Test
    void shouldRejectModifiedToken() {
        var token = tokenProvider.generateToken(UUID.randomUUID(), UUID.randomUUID());

        assertThat(tokenProvider.isTokenValid(token + "invalid")).isFalse();
    }

    @Test
    void shouldReadUserAndRoleIdsFromToken() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        var token = tokenProvider.generateToken(userId, roleId);

        assertThat(tokenProvider.getAuthenticatedUser(token))
                .isEqualTo(new AuthenticatedUser(userId, roleId));
    }
}
