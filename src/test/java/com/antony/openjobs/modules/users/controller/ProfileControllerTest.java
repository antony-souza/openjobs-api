package com.antony.openjobs.modules.users.controller;

import com.antony.openjobs.config.security.AuthenticatedUser;
import com.antony.openjobs.modules.users.usecase.ProfileResponse;
import com.antony.openjobs.modules.users.usecase.findprofile.FindProfileUseCase;
import com.antony.openjobs.modules.users.usecase.updateprofile.UpdateProfileRequest;
import com.antony.openjobs.modules.users.usecase.updateprofile.UpdateProfileUseCase;
import com.antony.openjobs.utils.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProfileControllerTest {
    @Test void bindsMultipartAvatarAndUsesAuthenticatedUserInsteadOfSubmittedIds() throws Exception {
        var useCase = mock(UpdateProfileUseCase.class); var userId = UUID.randomUUID(); var roleId = UUID.randomUUID();
        var response = new ProfileResponse(userId, "Maria", "maria@example.com", "maria", "https://example.com/photo.png", "Candidato");
        when(useCase.execute(eq(userId), any())).thenReturn(response);
        var mvc = MockMvcBuilders.standaloneSetup(new ProfileController(mock(FindProfileUseCase.class), useCase)).setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    public boolean supportsParameter(MethodParameter parameter) { return parameter.getParameterType() == AuthenticatedUser.class; }
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container, NativeWebRequest request, WebDataBinderFactory binder) { return new AuthenticatedUser(userId, roleId); }
                }).build();

        mvc.perform(multipart("/v1/users/me").file(new MockMultipartFile("avatar", "photo.png", "image/png", new byte[]{1, 2}))
                .param("name", "Maria").param("email", "maria@example.com").param("username", "maria")
                .param("removeAvatar", "false").param("password", "").param("userId", UUID.randomUUID().toString()).param("roleId", UUID.randomUUID().toString())
                .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.avatarUrl").value("https://example.com/photo.png"));
        var request = ArgumentCaptor.forClass(UpdateProfileRequest.class); verify(useCase).execute(eq(userId), request.capture());
        assertThat(request.getValue().avatar().getOriginalFilename()).isEqualTo("photo.png");
        assertThat(request.getValue().removeAvatar()).isFalse();
        assertThat(request.getValue().password()).isNull();
    }
}
