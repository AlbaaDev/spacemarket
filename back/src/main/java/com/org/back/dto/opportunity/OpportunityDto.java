package com.org.back.dto.opportunity;

import java.time.LocalDate;
import java.util.Map;

import com.org.back.enums.OpportunityStatus;

public record OpportunityDto(
        Long id,
        String name,
        String businessName,
        Long value,
        OpportunityStatus status,
        LocalDate closeDate,
        PrincipalContact principalContact,
        Map<String, Object> customValues) {

    public record PrincipalContact(Long id, String firstName, String lastName) {}
}
