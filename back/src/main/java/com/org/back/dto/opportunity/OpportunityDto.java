package com.org.back.dto.opportunity;

import java.time.LocalDate;
import java.util.Map;

import com.org.back.enums.OpportunityStatus;
import com.org.back.enums.Step;

public record OpportunityDto(
        Long id,
        String name,
        String businessName,
        Long value,
        OpportunityStatus status,
        LocalDate closeDate,
        PrincipalContact principalContact,
        Map<String, Object> customValues,
        Step step) {

    public record PrincipalContact(Long id, String firstName, String lastName) {}
}
