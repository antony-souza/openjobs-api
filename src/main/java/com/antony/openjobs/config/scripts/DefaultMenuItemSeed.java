package com.antony.openjobs.config.scripts;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.antony.openjobs.modules.menuitens.model.MenuItemEntity;
import com.antony.openjobs.modules.menuitens.repository.IMenuItemRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Order(3)
public class DefaultMenuItemSeed implements ApplicationRunner {
    private static final List<MenuDefinition> DEFAULT_MENUS = List.of(
            new MenuDefinition("Início", "House", "/home"),
            new MenuDefinition("Vagas", "BriefcaseBusiness", "/jobs"),
            new MenuDefinition("Empresas", "Building2", "/companies"),
            new MenuDefinition("Feed", "Newspaper", "/feed"),
            new MenuDefinition("Mensagens", "MessageCircle", "/messages"),
            new MenuDefinition("Candidaturas", "ClipboardList", "/applications"));

    private final IMenuItemRepository menuItemRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (MenuDefinition menu : DEFAULT_MENUS) {
            if (menuItemRepository.existsByPath(menu.path())) {
                continue;
            }

            MenuItemEntity menuItem = new MenuItemEntity();
            menuItem.setTitle(menu.title());
            menuItem.setIconName(menu.iconName());
            menuItem.setPath(menu.path());
            menuItemRepository.save(menuItem);
        }
    }

    private record MenuDefinition(String title, String iconName, String path) {
    }
}
