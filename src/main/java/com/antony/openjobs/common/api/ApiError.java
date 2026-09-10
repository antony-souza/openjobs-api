package com.antony.openjobs.common.api;

public record ApiError(
        String field,
        String message
) {
}
