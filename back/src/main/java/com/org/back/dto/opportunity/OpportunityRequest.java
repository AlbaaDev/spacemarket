package com.org.back.dto.opportunity;

import java.time.LocalDate;
import java.util.Map;

import com.org.back.enums.OpportunityStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record OpportunityRequest(
        @NotBlank String name,
        @NotBlank String businessName,
        @NotNull @PositiveOrZero Long value,
        OpportunityStatus status,
        LocalDate closeDate,
        @NotNull Long principalContactId,
        Map<String, Object> customValues) {}
