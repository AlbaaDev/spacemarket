package com.org.back.dto.user;

import java.util.List;

public record ContactDto(
        Long id,
        String firstName,
        String lastName,
        List<ContactPhoneDto> phones,
        List<ContactEmailDto> emails,
        CompanyDto company,
        String city,
        String address,
        String country) {}
