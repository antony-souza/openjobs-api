package com.antony.openjobs.modules.menuitens.controller;

import com.antony.openjobs.modules.menuitens.repository.IMenuItemRepository;
import com.antony.openjobs.modules.menuitens.usecase.create.CreateMenuItemRequest;
import com.antony.openjobs.modules.menuitens.usecase.create.CreateMenuItemResponse;
import com.antony.openjobs.modules.menuitens.usecase.create.CreateMenuItemUseCase;
import com.antony.openjobs.services.pagination.PaginationService;
import com.antony.openjobs.utils.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MenuItemControllerTest {

    @Test
    void createsMenuItemFromJson() throws Exception {
        var useCase = mock(CreateMenuItemUseCase.class);
        var controller = new MenuItemController(
                mock(IMenuItemRepository.class), mock(PaginationService.class), useCase);
        var mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        var request = new CreateMenuItemRequest("Vagas", "BriefcaseBusiness", "/jobs");
        when(useCase.execute(request))
                .thenReturn(new CreateMenuItemResponse("Menu item created successfully"));

        mvc.perform(post("/v1/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Vagas","iconName":"BriefcaseBusiness","path":"/jobs"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.message").value("Menu item created successfully"));

        verify(useCase).execute(request);
    }

    @Test
    void rejectsBlankFieldsBeforeCreating() throws Exception {
        var useCase = mock(CreateMenuItemUseCase.class);
        var controller = new MenuItemController(
                mock(IMenuItemRepository.class), mock(PaginationService.class), useCase);
        var mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mvc.perform(post("/v1/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"  ","iconName":"","path":null}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors.length()").value(3));

        verify(useCase, never()).execute(any());
    }

    @Test
    void rejectsMissingBodyBeforeCreating() throws Exception {
        var useCase = mock(CreateMenuItemUseCase.class);
        var controller = new MenuItemController(
                mock(IMenuItemRepository.class), mock(PaginationService.class), useCase);
        var mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mvc.perform(post("/v1/menu-items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors[0].message")
                        .value("O corpo da requisição é inválido"));

        verify(useCase, never()).execute(any());
    }

    @Test
    void returnsConflictWhenPathAlreadyExists() throws Exception {
        var useCase = mock(CreateMenuItemUseCase.class);
        var controller = new MenuItemController(
                mock(IMenuItemRepository.class), mock(PaginationService.class), useCase);
        var mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        var request = new CreateMenuItemRequest("Vagas", "BriefcaseBusiness", "/jobs");
        when(useCase.execute(request)).thenThrow(new ResponseStatusException(
                HttpStatus.CONFLICT, "Um menu item já existe com o mesmo path"));

        mvc.perform(post("/v1/menu-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Vagas","iconName":"BriefcaseBusiness","path":"/jobs"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Um menu item já existe com o mesmo path"));

        verify(useCase).execute(request);
    }
}
