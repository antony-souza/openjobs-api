package com.antony.openjobs.modules.menuitens.usecase.create;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.antony.openjobs.modules.menuitens.model.MenuItemEntity;
import com.antony.openjobs.modules.menuitens.repository.IMenuItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateMenuItemUseCase {
    private final IMenuItemRepository menuItemRepository;

    @Transactional 
    public CreateMenuItemResponse execute(CreateMenuItemRequest request) {
        if (menuItemRepository.existsByPath(request.path())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, 
                "Um menu item já existe com o mesmo path"
            );
        }

        var menuItem = new MenuItemEntity();

        menuItem.setTitle(request.title());
        menuItem.setIconName(request.iconName());
        menuItem.setPath(request.path());

        menuItemRepository.save(menuItem);

        return new CreateMenuItemResponse("Menu item created successfully");
    }
}
