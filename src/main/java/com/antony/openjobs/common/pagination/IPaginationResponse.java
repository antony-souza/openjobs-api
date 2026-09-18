package com.antony.openjobs.common.pagination;

import org.springframework.data.domain.Page;

import java.util.List;

public record PaginationResponse<T>(
        int page,
        int size,
        long total,
        List<T> items
) {
    public static <T> PaginationResponse<T> from(Page<T> result) {
        return new PaginationResponse<>(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getContent()
        );
    }
}
