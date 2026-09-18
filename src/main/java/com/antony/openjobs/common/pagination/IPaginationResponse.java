package com.antony.openjobs.common.pagination;

import org.springframework.data.domain.Page;

import java.util.List;

public record IPaginationResponse<T>(
        int page,
        int size,
        long total,
        List<T> items
) {
    public static <T> IPaginationResponse<T> from(Page<T> result) {
        return new IPaginationResponse<>(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getContent()
        );
    }
}
