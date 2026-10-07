package com.org.back.dto.interaction;

import java.time.LocalDate;

import com.org.back.enums.InteractionType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InteractionRequest(
        @NotNull InteractionType type,
        @NotNull LocalDate occurredOn,
        @Size(max = 2000) String note,
        Long opportunityId) {}
