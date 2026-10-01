package com.antony.openjobs.config.scripts;

import java.util.Map;
import java.util.Objects;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import com.antony.openjobs.modules.permissions.model.Permission;
import com.antony.openjobs.modules.permissions.model.PermissionEntity;
import com.antony.openjobs.modules.permissions.repository.IPermissionRepository;

import lombok.RequiredArgsConstructor;

// Para rodar a seed, descomente a anotação @Component e execute a aplicação.
// @Component
@RequiredArgsConstructor
@Order(1)
public class DefaultPermissionSeed implements ApplicationRunner {
    private static final Map<String, String> ACTION_NAMES = Map.of(
            "READ", "Consultar",
            "CREATE", "Criar",
            "UPDATE", "Atualizar",
            "DELETE", "Excluir");

    private static final Map<String, String> RESOURCE_NAMES = Map.of(
            "PERMISSION", "permissões",
            "ROLEPERMISSION", "vínculos de permissões a perfis",
            "USER", "usuários",
            "ROLE", "perfis",
            "POST", "publicações",
            "LIKE", "curtidas",
            "JOB", "vagas",
            "APPLICATION", "candidaturas",
            "MENUITEM", "itens de menu",
            "MENUROLEITEM", "vínculos de itens de menu a perfis");

    private final IPermissionRepository permissionRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (Permission permission : Permission.values()) {
            var existingPermission = permissionRepository.findByCode(permission.getCode());

            if (existingPermission.isPresent()) {
                PermissionEntity entity = existingPermission.get();
                boolean changed = false;

                if (entity.getDeletedAt() != null) {
                    entity.setDeletedAt(null);
                    changed = true;
                }

                String name = createName(permission);
                if (!Objects.equals(entity.getName(), name)) {
                    entity.setName(name);
                    changed = true;
                }

                String description = createDescription(permission);
                if (!Objects.equals(entity.getDescription(), description)) {
                    entity.setDescription(description);
                    changed = true;
                }

                if (changed) {
                    permissionRepository.save(entity);
                }
                continue;
            }

            PermissionEntity entity = new PermissionEntity();
            entity.setCode(permission.getCode());
            entity.setName(createName(permission));
            entity.setDescription(createDescription(permission));
            permissionRepository.save(entity);
        }
    }

    private String createName(Permission permission) {
        PermissionLabels labels = getLabels(permission);
        if (labels == null) {
            return permission.getCode();
        }

        return labels.actionName() + " " + labels.resourceName();
    }

    private String createDescription(Permission permission) {
        PermissionLabels labels = getLabels(permission);
        if (labels == null) {
            return "Permissão de sistema " + permission.getCode() + ".";
        }

        return "Permite " + labels.actionName().toLowerCase() + " " + labels.resourceName() + ".";
    }

    private PermissionLabels getLabels(Permission permission) {
        String code = permission.getCode();
        int separatorIndex = code.lastIndexOf('_');
        if (separatorIndex < 1 || separatorIndex == code.length() - 1) {
            return null;
        }

        String actionName = ACTION_NAMES.get(code.substring(separatorIndex + 1));
        String resourceName = RESOURCE_NAMES.get(code.substring(0, separatorIndex));
        if (actionName == null || resourceName == null) {
            return null;
        }

        return new PermissionLabels(actionName, resourceName);
    }

    private record PermissionLabels(String actionName, String resourceName) {
    }
}
