package com.antony.openjobs.modules.users.usecase.findall;

import com.antony.openjobs.common.pagination.PaginationResponse;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.UserRepository;
import com.antony.openjobs.modules.users.usecase.findall.mapper.UserMapper;
import com.antony.openjobs.modules.users.usecase.findall.mapper.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindAllUseCase {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public PaginationResponse<UserResponse> findAllByDeletedAtIsNull(Pageable pageable) {
        Page<UserResponse> page = userRepository
                .findAllByDeletedAtIsNull(pageable)
                .map(userMapper::toResponse);

        return PaginationResponse.from(page);
    }
}
