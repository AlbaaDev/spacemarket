package com.org.back.dto.user;

import java.util.List;

public record CompanyDto(
    Long id,
    String name,
    String country,
    String city,
    String address,
    String industry,
    List<ContactDto> contacts
) {}
