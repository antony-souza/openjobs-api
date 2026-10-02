package com.antony.openjobs.modules.menuitens.usecase.create;

import com.antony.openjobs.modules.menuitens.model.MenuItemEntity;
import com.antony.openjobs.modules.menuitens.repository.IMenuItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateMenuItemUseCaseTest {

    @Mock
    private IMenuItemRepository menuItemRepository;

    @InjectMocks
    private CreateMenuItemUseCase useCase;

    @Captor
    private ArgumentCaptor<MenuItemEntity> menuItemCaptor;

    @Test
    void createsMenuItemWithProvidedFields() {
        var request = new CreateMenuItemRequest("Vagas", "BriefcaseBusiness", "/jobs");

        CreateMenuItemResponse response = useCase.execute(request);

        assertThat(response.message()).isEqualTo("Menu item created successfully");
        verify(menuItemRepository).existsByPath("/jobs");
        verify(menuItemRepository).save(menuItemCaptor.capture());
        MenuItemEntity saved = menuItemCaptor.getValue();
        assertThat(saved.getTitle()).isEqualTo("Vagas");
        assertThat(saved.getIconName()).isEqualTo("BriefcaseBusiness");
        assertThat(saved.getPath()).isEqualTo("/jobs");
    }

    @Test
    void rejectsDuplicatePathWithoutSaving() {
        var request = new CreateMenuItemRequest("Vagas", "BriefcaseBusiness", "/jobs");
        when(menuItemRepository.existsByPath("/jobs")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> {
                    var responseException = (ResponseStatusException) exception;
                    assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(responseException.getReason())
                            .isEqualTo("Um menu item já existe com o mesmo path");
                });

        verify(menuItemRepository, never()).save(any());
    }
}
