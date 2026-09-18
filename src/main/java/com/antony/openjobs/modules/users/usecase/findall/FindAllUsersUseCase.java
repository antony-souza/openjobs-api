package com.antony.openjobs.modules.users.usecase.findall;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindAllUsersUseCase {
    private final UserRepository userRepository;

    public IPaginationResponse<FindAllUsersProjection> execute(Pageable pageable) {
        Page<FindAllUsersProjection> page = userRepository
                .findAllByDeletedAtIsNull(pageable);

        return IPaginationResponse.from(page);
    }
}
