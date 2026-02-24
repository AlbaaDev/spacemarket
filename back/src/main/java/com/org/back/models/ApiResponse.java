package com.org.back.models;

import java.util.List;

public record ApiResponse<T>(
        int statusCode,
        String message,
        List<String> errors,
        T data,
        long timesTamp,
        String path) {
}
