package com.org.back.dto.user;

import com.org.back.enums.PhoneType;

public record ContactPhoneDto(
    Long id,
    String phone,
    PhoneType type,
    boolean isPrimary
) {}
