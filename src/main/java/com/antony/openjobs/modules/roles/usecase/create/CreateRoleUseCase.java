package com.antony.openjobs.modules.roles.usecase.create;

import com.antony.openjobs.modules.roles.model.RoleEntity;
import com.antony.openjobs.modules.roles.repository.RoleRepository;
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

    public CreateRoleResponse execute(CreateRoleRequest request) {

        if (roleRepository.existsByNameAndCode(request.name().trim(), request.code().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role já cadastrada");
        }

        RoleEntity role = new RoleEntity();
        role.setName(request.name().trim());
        role.setCode(request.code().trim());
        role.setLevel(request.level());

        roleRepository.save(role);

        return new CreateRoleResponse("Role cadastrada com sucesso!");
    }
}
