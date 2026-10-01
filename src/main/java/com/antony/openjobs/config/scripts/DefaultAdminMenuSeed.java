package com.antony.openjobs.config.scripts;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.antony.openjobs.modules.menuitens.model.MenuItemEntity;
import com.antony.openjobs.modules.menuitens.repository.IMenuItemRepository;
import com.antony.openjobs.modules.rolemenu.model.RoleMenuEntity;
import com.antony.openjobs.modules.rolemenu.repository.IRoleMenuRepository;
import com.antony.openjobs.modules.roles.model.RoleEntity;
import com.antony.openjobs.modules.roles.repository.IRoleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Order(4)
public class DefaultAdminMenuSeed implements ApplicationRunner {
    private final IRoleRepository roleRepository;
    private final IMenuItemRepository menuItemRepository;
    private final IRoleMenuRepository roleMenuRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        roleRepository.findByCode(DefaultUserSeed.ADMIN_ROLE_CODE)
                .filter(role -> role.getDeletedAt() == null)
                .ifPresent(this::linkMenusToAdminRole);
    }

    private void linkMenusToAdminRole(RoleEntity adminRole) {
        for (MenuItemEntity menuItem : menuItemRepository.findAllByDeletedAtIsNull()) {
            var existingLink = roleMenuRepository.findByRole_IdAndMenuItem_Id(
                    adminRole.getId(),
                    menuItem.getId());

            if (existingLink.isPresent()) {
                RoleMenuEntity link = existingLink.get();
                if (link.getDeletedAt() != null) {
                    link.setDeletedAt(null);
                    roleMenuRepository.save(link);
                }
                continue;
            }

            RoleMenuEntity link = new RoleMenuEntity();
            link.setRole(adminRole);
            link.setMenuItem(menuItem);
            roleMenuRepository.save(link);
        }
    }
}
