package com.org.back.dto.customfield;

import java.util.List;

import com.org.back.enums.CustomFieldTarget;
import com.org.back.enums.CustomFieldType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Target and type are fixed at creation; an update only changes the name and the choices. */
public record CustomFieldRequest(
        @NotBlank @Size(max = 64) String name,
        @NotNull CustomFieldTarget target,
        @NotNull CustomFieldType type,
        List<@NotBlank @Size(max = 64) String> options) {}
