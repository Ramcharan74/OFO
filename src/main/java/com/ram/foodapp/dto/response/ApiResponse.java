package com.ram.foodapp.dto.response;

import com.ram.foodapp.enums.ErrorCode;

import java.time.Instant;

public record ApiResponse<T>(
        String status,
        String message,
        T data,
        Instant timestamp,
        String errorCode
) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(
                "SUCCESS",
                message,
                data,
                Instant.now(),
                null
                );
    }

    public static <T> ApiResponse<T> error(String message, ErrorCode errorCode) {
        return new ApiResponse<>("ERROR", message, null,
                Instant.now(),
                errorCode.name());
    }
}