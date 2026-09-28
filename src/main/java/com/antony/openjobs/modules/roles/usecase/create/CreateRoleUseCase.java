package com.antony.openjobs.modules.roles.usecase.create;

import com.antony.openjobs.modules.roles.model.RoleEntity;
import com.antony.openjobs.modules.roles.repository.IRoleRepository;
import com.antony.openjobs.modules.roles.services.RoleValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateRoleUseCase {
    private final IRoleRepository roleRepository;
    private final RoleValidationService roleValidationService;

    public CreateRoleResponse execute(CreateRoleRequest createRoleRequest) {

        roleValidationService.validateDuplicateRole(
                createRoleRequest.code().trim(),
                null
        );

        RoleEntity role = new RoleEntity();

        role.setName(createRoleRequest.name().trim());
        role.setCode(createRoleRequest.code().trim());
        role.setLevel(createRoleRequest.level());

        roleRepository.save(role);

        return new CreateRoleResponse("Role cadastrada com sucesso!");
    }
}
