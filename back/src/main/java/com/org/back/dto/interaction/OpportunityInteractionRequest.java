package com.org.back.dto.interaction;

import java.time.LocalDate;

import com.org.back.enums.InteractionType;
import com.org.back.enums.Step;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// An Interaction logged from an Opportunity: the Opportunity comes from the path, the Contact from the body.
public record OpportunityInteractionRequest(
        @NotNull InteractionType type,
        @NotNull LocalDate occurredOn,
        @Size(max = 2000) String note,
        @NotNull Long contactId,
        Step step) {}
