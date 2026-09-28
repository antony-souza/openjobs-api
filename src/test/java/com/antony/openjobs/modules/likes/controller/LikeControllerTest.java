package com.antony.openjobs.modules.likes.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.antony.openjobs.config.security.AuthenticatedUser;
import com.antony.openjobs.modules.likes.usecase.upsert.UpsertLikeResponse;
import com.antony.openjobs.modules.likes.usecase.upsert.UpsertLikeUseCase;
import com.antony.openjobs.utils.GlobalExceptionHandler;

class LikeControllerTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldUsePostIdFromPathAndUserIdFromAuthenticatedUser() throws Exception {
        var useCase = mock(UpsertLikeUseCase.class);
        var controller = new LikeController(useCase);
        var mvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        var userId = UUID.randomUUID();
        var roleId = UUID.randomUUID();
        var postId = UUID.randomUUID();
        var authenticatedUser = new AuthenticatedUser(userId, roleId);

        when(useCase.execute(postId, userId))
                .thenReturn(new UpsertLikeResponse("Like created successfully for post"));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(authenticatedUser, null));

        mvc.perform(post("/v1/likes/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.message")
                        .value("Like created successfully for post"));

        verify(useCase).execute(postId, userId);
    }
}
