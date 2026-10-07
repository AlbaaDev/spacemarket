package com.org.back.dto.dashboard;

import java.time.LocalDate;

/** Figures for the requested Period and for the Period of equal length just before it. */
public record DashboardSummaryDto(Figures current, Figures previous) {

    public record Figures(LocalDate from, LocalDate to, long revenue, long interactions, long contactsReached) {}
}
