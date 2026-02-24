package com.org.back.dto.user;

public record CompanyDto(
    Long id,
    String name,
    String country,
    String city,
    String address
) {}
