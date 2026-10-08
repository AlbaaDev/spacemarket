package com.org.back.services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.back.dto.reporting.ReportingSummaryDto;
import com.org.back.dto.reporting.TimelinePointDto;
import com.org.back.repositories.InteractionRepository;
import com.org.back.repositories.OpportunityRepository;

@Service
public class ReportingService {

    private final OpportunityRepository opportunityRepository;
    private final InteractionRepository interactionRepository;

    public ReportingService(OpportunityRepository opportunityRepository, InteractionRepository interactionRepository) {
        this.opportunityRepository = opportunityRepository;
        this.interactionRepository = interactionRepository;
    }

    /**
     * Both bounds are inclusive. Without an explicit comparison Period (e.g. the previous calendar month),
     * the previous Period has the same number of days and ends the day before.
     */
    @Transactional(readOnly = true)
    public ReportingSummaryDto summary(Long userId, LocalDate from, LocalDate to, LocalDate compareFrom,
            LocalDate compareTo) {
        if (compareFrom == null || compareTo == null) {
            long days = ChronoUnit.DAYS.between(from, to) + 1;
            compareTo = from.minusDays(1);
            compareFrom = compareTo.minusDays(days - 1);
        }
        return new ReportingSummaryDto(figures(userId, from, to), figures(userId, compareFrom, compareTo));
    }

    @Transactional(readOnly = true)
    public List<TimelinePointDto> timeline(Long userId, LocalDate from, LocalDate to) {
        Map<LocalDate, long[]> byDay = new TreeMap<>();
        for (Object[] row : opportunityRepository.revenueByDay(userId, from, to)) {
            byDay.computeIfAbsent((LocalDate) row[0], d -> new long[2])[0] = ((Number) row[1]).longValue();
        }
        for (Object[] row : interactionRepository.countByDay(userId, from, to)) {
            byDay.computeIfAbsent((LocalDate) row[0], d -> new long[2])[1] = ((Number) row[1]).longValue();
        }
        return byDay.entrySet().stream()
                .map(e -> new TimelinePointDto(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .toList();
    }

    private ReportingSummaryDto.Figures figures(Long userId, LocalDate from, LocalDate to) {
        return new ReportingSummaryDto.Figures(
                from,
                to,
                opportunityRepository.sumRevenue(userId, from, to),
                interactionRepository.countInPeriod(userId, from, to),
                interactionRepository.countContactsReached(userId, from, to));
    }
}
