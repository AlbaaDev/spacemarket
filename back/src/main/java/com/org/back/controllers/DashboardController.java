package com.org.back.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.org.back.dto.dashboard.DashboardSummaryDto;
import com.org.back.dto.dashboard.TimelinePointDto;
import com.org.back.models.ApiResponse;
import com.org.back.models.ResponseUtil;
import com.org.back.models.User;
import com.org.back.services.DashboardService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryDto>> summary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate compareFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate compareTo,
            @AuthenticationPrincipal User user, HttpServletRequest request) {
        checkPeriod(from, to);
        if (compareFrom != null && compareTo != null) {
            checkPeriod(compareFrom, compareTo);
        }
        return ResponseEntity.ok(ResponseUtil.success(
                HttpStatus.OK.value(), "Success",
                dashboardService.summary(user.getId(), from, to, compareFrom, compareTo),
                request.getRequestURI()));
    }

    @GetMapping("/timeline")
    public ResponseEntity<ApiResponse<List<TimelinePointDto>>> timeline(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @AuthenticationPrincipal User user, HttpServletRequest request) {
        checkPeriod(from, to);
        return ResponseEntity.ok(ResponseUtil.success(
                HttpStatus.OK.value(), "Success", dashboardService.timeline(user.getId(), from, to),
                request.getRequestURI()));
    }

    private static void checkPeriod(LocalDate from, LocalDate to) {
        if (to.isBefore(from)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'to' must not be before 'from'");
        }
    }
}
