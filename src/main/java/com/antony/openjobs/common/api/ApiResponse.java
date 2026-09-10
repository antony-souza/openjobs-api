package com.antony.openjobs.common.api;

import java.util.List;

public record ApiResponse<T>(
        boolean success,
        T data,
        List<ApiError> errors
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, List.of());
    }

    public static ApiResponse<Void> failure(ApiError error) {
        return new ApiResponse<>(false, null, List.of(error));
    }

    public static ApiResponse<Void> failure(List<ApiError> errors) {
        return new ApiResponse<>(false, null, List.copyOf(errors));
    }
}
