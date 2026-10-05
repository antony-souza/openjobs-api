package com.antony.openjobs.config.scripts;

import com.antony.openjobs.modules.menuitens.model.MenuItemEntity;
import com.antony.openjobs.modules.menuitens.repository.IMenuItemRepository;
import com.antony.openjobs.modules.rolemenu.model.RoleMenuEntity;
import com.antony.openjobs.modules.rolemenu.repository.IRoleMenuRepository;
import com.antony.openjobs.modules.roles.repository.IRoleRepository;
import com.antony.openjobs.modules.roles.model.RoleEntity;
import com.antony.openjobs.modules.permissions.model.Permission;
import com.antony.openjobs.modules.permissions.model.PermissionEntity;
import com.antony.openjobs.modules.permissions.repository.IPermissionRepository;
import com.antony.openjobs.modules.rolepermissions.model.RolePermissionEntity;
import com.antony.openjobs.modules.rolepermissions.repository.IRolePermissionRepository;
import com.antony.openjobs.utils.RoleCodeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(3)
public class CandidateHomeSeed implements ApplicationRunner {
    private final IRoleRepository roleRepository;
    private final IMenuItemRepository menuItemRepository;
    private final IRoleMenuRepository roleMenuRepository;
    private final IPermissionRepository permissionRepository;
    private final IRolePermissionRepository rolePermissionRepository;

    private record MenuPreset(String title, String iconName, String path) {}

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        var role = roleRepository.findByCodeAndDeletedAtIsNull(RoleCodeUtils.CANDIDATE).orElseThrow();
        var menus = List.of(
                new MenuPreset("Início", "Home", "/home"),
                new MenuPreset("Explorar vagas", "BriefcaseBusiness", "/vagas"),
                new MenuPreset("Meu perfil", "UserRound", "/perfil"),
                new MenuPreset("Comunidade", "UsersRound", "/home#feed")
        );

        for (var menu : menus) {
            seedMenu(role, menu);
        }

        for (var permission : List.of(Permission.APPLICATION_CREATE, Permission.APPLICATION_READ, Permission.APPLICATION_DELETE)) {
            seedPermission(role, permission.getCode());
        }
    }

    private void seedMenu(RoleEntity role, MenuPreset preset) {
        var menu = menuItemRepository.findByPath(preset.path()).orElseGet(() ->
                preset.path().equals("/vagas")
                        ? menuItemRepository.findByPath("/home#vagas").orElseGet(MenuItemEntity::new)
                        : new MenuItemEntity());
        menu.setTitle(preset.title());
        menu.setIconName(preset.iconName());
        menu.setPath(preset.path());
        menu.setDeletedAt(null);
        menu = menuItemRepository.save(menu);

        var link = roleMenuRepository.findByRole_IdAndMenuItem_Id(role.getId(), menu.getId())
                .orElseGet(RoleMenuEntity::new);
        link.setRole(role);
        link.setMenuItem(menu);
        link.setDeletedAt(null);
        roleMenuRepository.save(link);
    }

    private void seedPermission(RoleEntity role, String code) {
        var permission = permissionRepository.findByCode(code).orElseGet(() -> {
            var created = new PermissionEntity();
            created.setCode(code);
            created.setName(code);
            created.setDescription("Gerenciar as próprias candidaturas");
            return created;
        });
        permission.setDeletedAt(null);
        permission = permissionRepository.save(permission);

        var link = rolePermissionRepository.findByRoleIdAndPermissionId(role.getId(), permission.getId())
                .orElseGet(RolePermissionEntity::new);
        link.setRole(role);
        link.setPermission(permission);
        link.setDeletedAt(null);
        rolePermissionRepository.save(link);
    }
}
