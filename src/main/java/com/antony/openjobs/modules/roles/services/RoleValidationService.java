package com.antony.openjobs.modules.roles.services;

import com.antony.openjobs.modules.roles.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleValidationService {
    private final RoleRepository roleRepository;

    public void validateDuplicateRole(String name, String code, UUID roleId) {
        boolean roleExists = roleId == null
                ? roleRepository.existsByNameAndCodeAndDeletedAtIsNull(name, code)
                : roleRepository.existsByIdAndCodeAndNameAndDeletedAtIsNull(
                roleId, code, name
        );

        if (roleExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Role já cadastrada"
            );
        }
    }
}
