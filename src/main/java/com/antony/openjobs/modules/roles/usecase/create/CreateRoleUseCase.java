package com.antony.openjobs.modules.roles.usecase.create;

import com.antony.openjobs.modules.roles.model.RoleEntity;
import com.antony.openjobs.modules.roles.repository.RoleRepository;
import com.antony.openjobs.modules.roles.services.RoleValidationService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreateRoleUseCase {
    private final RoleRepository roleRepository;
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
