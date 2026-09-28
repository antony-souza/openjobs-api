package com.antony.openjobs.modules.likes.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.antony.openjobs.modules.likes.usecase.LikeResponse;
import com.antony.openjobs.modules.likes.usecase.create.CreateLikeUseCase;
import com.antony.openjobs.modules.likes.usecase.delete.DeleteLikeUseCase;
import com.antony.openjobs.modules.likes.usecase.update.UpdateLikeUseCase;
import com.antony.openjobs.utils.GlobalExceptionHandler;

class LikeControllerTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldUseCrudRoutesWithPostIdFromPathAndUserFromToken() throws Exception {
        var createUseCase = mock(CreateLikeUseCase.class);
        var updateUseCase = mock(UpdateLikeUseCase.class);
        var deleteUseCase = mock(DeleteLikeUseCase.class);
        var controller = new LikeController(createUseCase, updateUseCase, deleteUseCase);
        var mvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        var userId = UUID.randomUUID();
        var postId = UUID.randomUUID();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new AuthenticatedUser(userId, UUID.randomUUID()), null));

        when(createUseCase.execute(postId, userId)).thenReturn(new LikeResponse("created"));
        when(updateUseCase.execute(postId, userId)).thenReturn(new LikeResponse("updated"));
        when(deleteUseCase.execute(postId, userId)).thenReturn(new LikeResponse("deleted"));

        mvc.perform(post("/v1/likes/{postId}", postId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.message").value("created"));
        mvc.perform(put("/v1/likes/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("updated"));
        mvc.perform(delete("/v1/likes/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("deleted"));

        verify(createUseCase).execute(postId, userId);
        verify(updateUseCase).execute(postId, userId);
        verify(deleteUseCase).execute(postId, userId);
    }
}
