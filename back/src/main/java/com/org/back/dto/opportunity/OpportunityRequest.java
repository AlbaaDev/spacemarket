package com.org.back.dto.opportunity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.org.back.enums.OpportunityStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/** Company and Contacts are optional: companyId may be null and contactIds null or empty. */
public record OpportunityRequest(
        @NotBlank String name,
        Long companyId,
        @NotNull @PositiveOrZero Long value,
        OpportunityStatus status,
        LocalDate closeDate,
        List<Long> contactIds,
        Map<String, Object> customValues) {}
