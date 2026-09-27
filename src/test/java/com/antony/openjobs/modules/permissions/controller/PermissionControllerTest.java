package com.antony.openjobs.modules.permissions.controller;

import com.antony.openjobs.modules.permissions.repository.PermissionRepository;
import com.antony.openjobs.modules.permissions.usecase.create.CreatePermissionRequest;
import com.antony.openjobs.modules.permissions.usecase.create.CreatePermissionResponse;
import com.antony.openjobs.modules.permissions.usecase.create.CreatePermissionUseCase;
import com.antony.openjobs.services.pagination.PaginationService;
import com.antony.openjobs.utils.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PermissionControllerTest {

    @Test
    void bindsJsonToTheCreatePermissionRequest() throws Exception {
        var useCase = mock(CreatePermissionUseCase.class);
        var controller = new PermissionController(
                useCase, mock(PermissionRepository.class), mock(PaginationService.class)
        );
        var mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        var request = new CreatePermissionRequest(
                "Criar vagas", "JOB_CREATE", "Permite publicar vagas."
        );
        when(useCase.execute(request)).thenReturn(new CreatePermissionResponse("Permissão criada"));

        mvc.perform(post("/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Criar vagas","code":"JOB_CREATE","description":"Permite publicar vagas."}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.message").value("Permissão criada"));

        verify(useCase).execute(request);
    }

    @Test
    void rejectsMissingPermissionFieldsBeforeCreating() throws Exception {
        var useCase = mock(CreatePermissionUseCase.class);
        var controller = new PermissionController(
                useCase, mock(PermissionRepository.class), mock(PaginationService.class)
        );
        var mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mvc.perform(post("/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors.length()").value(3));

        verify(useCase, never()).execute(any());
    }
}
