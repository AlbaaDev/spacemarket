package com.org.back.dto.user;

import java.util.List;
import java.util.Map;

public record CompanyDto(
    Long id,
    String name,
    String country,
    String city,
    String address,
    String industry,
    List<ContactDto> contacts,
    Map<String, Object> customValues
) {}
