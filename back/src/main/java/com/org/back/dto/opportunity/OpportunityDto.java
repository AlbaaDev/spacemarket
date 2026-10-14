package com.org.back.dto.opportunity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.org.back.enums.OpportunityStatus;
import com.org.back.enums.Step;

public record OpportunityDto(
        Long id,
        String name,
        CompanyRef company,
        Long value,
        OpportunityStatus status,
        LocalDate closeDate,
        List<ContactRef> contacts,
        Map<String, Object> customValues,
        Step step) {

    public record CompanyRef(Long id, String name) {}

    public record ContactRef(Long id, String firstName, String lastName) {}
}
