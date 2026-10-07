package com.org.back.dto.interaction;

import java.time.LocalDate;

import com.org.back.enums.InteractionType;

public record InteractionDto(
        Long id,
        InteractionType type,
        LocalDate occurredOn,
        String note,
        Long contactId,
        Long opportunityId) {}
