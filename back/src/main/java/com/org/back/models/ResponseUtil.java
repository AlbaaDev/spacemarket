package com.org.back.models;

import java.util.List;

public class ResponseUtil {

    public static <T> ApiResponse<T> success(int statusCode, String message, T data, String path) {
        return new ApiResponse<>(
                statusCode,
                message,
                null,
                data,
                System.currentTimeMillis(),
                path);
    }

    public static <T> ApiResponse<T> error(int statusCode, List<String> errors,  String path) {
        return new ApiResponse<>(
                statusCode,
                null,
                errors,
                null,
                System.currentTimeMillis(),
                path);
    }
}
