package com.antony.openjobs.config.scripts;

import com.antony.openjobs.modules.roles.model.RoleEntity;
import com.antony.openjobs.modules.roles.repository.IRoleRepository;
import com.antony.openjobs.utils.RoleCodeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Order(1)
public class CandidateRoleSeed implements ApplicationRunner {
    private final IRoleRepository roleRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        RoleEntity role = roleRepository.findByCode(RoleCodeUtils.CANDIDATE).orElseGet(RoleEntity::new);
        role.setName("Candidato");
        role.setCode(RoleCodeUtils.CANDIDATE);
        role.setLevel(0);
        role.setDeletedAt(null);
        roleRepository.save(role);
    }
}
