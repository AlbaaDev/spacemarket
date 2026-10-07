package com.org.back.dto.customfield;

import java.util.List;

import com.org.back.enums.CustomFieldTarget;
import com.org.back.enums.CustomFieldType;

public record CustomFieldDto(Long id, String name, CustomFieldTarget target, CustomFieldType type, List<String> options) {}
