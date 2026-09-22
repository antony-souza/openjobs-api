package com.antony.openjobs.services.pagination;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.common.repositories.IBaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaginationService {
    @Transactional(readOnly = true)
    public <T, P> IPaginationResponse<P> execute(
            IBaseRepository<T, UUID> repository,
            Pageable pageable,
            Class<P> projection
    ) {
        Page<P> page = repository
                .findAllByDeletedAtIsNull(pageable, projection);

        return IPaginationResponse.from(page);
    }
}
