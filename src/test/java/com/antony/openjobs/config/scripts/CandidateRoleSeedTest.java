package com.antony.openjobs.config.scripts;

import com.antony.openjobs.modules.roles.model.RoleEntity;
import com.antony.openjobs.modules.roles.repository.IRoleRepository;
import com.antony.openjobs.utils.RoleCodeUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandidateRoleSeedTest {
    @Mock
    private IRoleRepository roleRepository;

    @InjectMocks
    private CandidateRoleSeed seed;

    @Test
    void createsCandidateRoleWhenMissing() {
        when(roleRepository.findByCode(RoleCodeUtils.CANDIDATE)).thenReturn(Optional.empty());

        seed.run(null);

        var captor = ArgumentCaptor.forClass(RoleEntity.class);
        verify(roleRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Candidato");
        assertThat(captor.getValue().getCode()).isEqualTo(RoleCodeUtils.CANDIDATE);
        assertThat(captor.getValue().getLevel()).isZero();
    }

    @Test
    void restoresExistingCandidateRoleWithoutCreatingAnother() {
        var role = new RoleEntity();
        role.setDeletedAt(LocalDateTime.now());
        when(roleRepository.findByCode(RoleCodeUtils.CANDIDATE)).thenReturn(Optional.of(role));

        seed.run(null);

        verify(roleRepository).save(role);
        assertThat(role.getDeletedAt()).isNull();
        assertThat(role.getName()).isEqualTo("Candidato");
    }
}
