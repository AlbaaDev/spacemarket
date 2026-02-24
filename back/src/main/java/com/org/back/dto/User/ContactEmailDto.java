package com.org.back.dto.user;

import com.org.back.enums.EmailType;

public record ContactEmailDto(
    Long id,
    String email,
    EmailType type,
    boolean isPrimary
) {}
