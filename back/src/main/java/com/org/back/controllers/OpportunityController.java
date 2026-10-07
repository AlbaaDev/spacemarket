package com.org.back.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.back.dto.opportunity.OpportunityDto;
import com.org.back.dto.opportunity.OpportunityRequest;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.interfaces.OpportunityService;
import com.org.back.models.ApiResponse;
import com.org.back.models.ResponseUtil;
import com.org.back.models.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;

    public OpportunityController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<OpportunityDto>>> getOpportunities(@AuthenticationPrincipal User user,
            HttpServletRequest request) {
        return ResponseEntity.ok(ResponseUtil.success(
                HttpStatus.OK.value(),
                "Success",
                opportunityService.getOpportunities(user.getId()),
                request.getRequestURI()));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<OpportunityDto>> addOpportunity(@Valid @RequestBody OpportunityRequest body,
            @AuthenticationPrincipal User user, HttpServletRequest request) throws EntityNotFoundException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseUtil.success(
                        HttpStatus.CREATED.value(),
                        "Opportunity created successfully",
                        opportunityService.addOpportunity(user.getId(), body),
                        request.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OpportunityDto>> updateOpportunity(@PathVariable Long id,
            @Valid @RequestBody OpportunityRequest body, @AuthenticationPrincipal User user,
            HttpServletRequest request) throws EntityNotFoundException {
        return ResponseEntity.ok(ResponseUtil.success(
                HttpStatus.OK.value(),
                "Opportunity updated successfully",
                opportunityService.updateOpportunity(user.getId(), id, body),
                request.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOpportunity(@PathVariable Long id, @AuthenticationPrincipal User user)
            throws EntityNotFoundException {
        opportunityService.deleteOpportunity(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
