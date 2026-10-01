package com.antony.openjobs.modules.menuitens.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.antony.openjobs.common.api.ApiResponse;
import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.config.security.RequiresPermission;
import com.antony.openjobs.modules.menuitens.repository.IMenuItemRepository;
import com.antony.openjobs.modules.menuitens.usecase.create.CreateMenuItemRequest;
import com.antony.openjobs.modules.menuitens.usecase.create.CreateMenuItemResponse;
import com.antony.openjobs.modules.menuitens.usecase.create.CreateMenuItemUseCase;
import com.antony.openjobs.modules.menuitens.usecase.findall.FindAllMenuItemsProjection;
import com.antony.openjobs.modules.permissions.model.Permission;
import com.antony.openjobs.services.pagination.PaginationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/menu-items")
@RequiredArgsConstructor
public class MenuItemController {
    private final IMenuItemRepository menuItemRepository;
    private final PaginationService paginationService;
    private final CreateMenuItemUseCase createMenuItemUseCase;

    @RequiresPermission(Permission.MENUITEM_CREATE)
    @PostMapping()
    public ResponseEntity<ApiResponse<CreateMenuItemResponse>> create(
            @Valid @RequestBody CreateMenuItemRequest request) {
        var response = createMenuItemUseCase.execute(request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @RequiresPermission(Permission.MENUITEM_READ)
    @GetMapping()
    public ResponseEntity<ApiResponse<IPaginationResponse<FindAllMenuItemsProjection>>> findAll(
            Pageable pageable) {
        var response = paginationService.execute(
                menuItemRepository,
                pageable,
                FindAllMenuItemsProjection.class);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
