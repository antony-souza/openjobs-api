package com.antony.openjobs.modules.rolepermissions.controller;

import com.antony.openjobs.modules.rolepermissions.repository.IRolePermissionRepository;
import com.antony.openjobs.modules.rolepermissions.usecase.create.CreateRolePermissionRequest;
import com.antony.openjobs.modules.rolepermissions.usecase.create.CreateRolePermissionResponse;
import com.antony.openjobs.modules.rolepermissions.usecase.create.CreateRolePermissionUseCase;
import com.antony.openjobs.services.pagination.PaginationService;
import com.antony.openjobs.utils.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RolePermissionsControllerTest {

    @Test
    void bindsJsonToTheCreateRolePermissionRequest() throws Exception {
        var useCase = mock(CreateRolePermissionUseCase.class);
        var controller = new RolePermissionsController(
                useCase, mock(IRolePermissionRepository.class), mock(PaginationService.class)
        );
        var mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        UUID roleId = UUID.randomUUID();
        UUID permissionId = UUID.randomUUID();
        var request = new CreateRolePermissionRequest(roleId, permissionId);
        when(useCase.execute(request))
                .thenReturn(new CreateRolePermissionResponse("Vínculo criado"));

        mvc.perform(post("/v1/role-permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"roleId":"%s","permissionId":"%s"}
                                """.formatted(roleId, permissionId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.message").value("Vínculo criado"));

        verify(useCase).execute(request);
    }

    @Test
    void rejectsMissingPermissionIdBeforeCreating() throws Exception {
        var useCase = mock(CreateRolePermissionUseCase.class);
        var controller = new RolePermissionsController(
                useCase, mock(IRolePermissionRepository.class), mock(PaginationService.class)
        );
        var mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mvc.perform(post("/v1/role-permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"roleId":"%s"}
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors[0].field").value("permissionId"));

        verify(useCase, never()).execute(any());
    }
}
