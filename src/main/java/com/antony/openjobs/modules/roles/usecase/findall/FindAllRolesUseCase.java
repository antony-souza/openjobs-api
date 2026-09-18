package com.antony.openjobs.modules.roles.usecase.findall;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.roles.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindAllRolesUseCase {
    private final RoleRepository roleRepository;

    public IPaginationResponse<FindAllRolesProjection> execute(Pageable pageable) {
        Page<FindAllRolesProjection> page = roleRepository.findAllByDeletedAtIsNull(pageable);
        return IPaginationResponse.from(page);
    }
}
