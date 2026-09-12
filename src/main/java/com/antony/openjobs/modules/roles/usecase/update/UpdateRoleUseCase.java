package com.antony.openjobs.modules.roles.usecase.update;

import com.antony.openjobs.modules.roles.model.RoleEntity;
import com.antony.openjobs.modules.roles.repository.RoleRepository;
import com.antony.openjobs.modules.roles.services.RoleValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateRoleUseCase {
    private final RoleRepository roleRepository;
    private final RoleValidationService roleValidationService;

    public UpdateRoleResponse execute(UUID roleId, UpdateRoleRequest updateRoleRequest) {
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Role não encontrada"
                ));

        roleValidationService.validateDuplicateRole(
                updateRoleRequest.code().trim(),
                roleId
        );

        role.setName(updateRoleRequest.name().trim());
        role.setCode(updateRoleRequest.code().trim());
        role.setLevel(updateRoleRequest.level());

        roleRepository.save(role);

        return new UpdateRoleResponse("Role atualizado com sucesso");
    }
}
