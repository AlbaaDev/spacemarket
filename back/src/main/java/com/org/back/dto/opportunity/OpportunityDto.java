package com.org.back.dto.opportunity;

import java.time.LocalDate;

import com.org.back.enums.OpportunityStatus;

public record OpportunityDto(
        Long id,
        String name,
        String businessName,
        Long value,
        OpportunityStatus status,
        LocalDate closeDate,
        PrincipalContact principalContact) {

    public record PrincipalContact(Long id, String firstName, String lastName) {}
}
