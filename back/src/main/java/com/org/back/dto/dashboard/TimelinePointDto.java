package com.org.back.dto.dashboard;

import java.time.LocalDate;

/** One day of activity; days without Revenue or Interactions are omitted. */
public record TimelinePointDto(LocalDate date, long revenue, long interactions) {}
