package com.org.back.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.back.models.Opportunity;
import com.org.back.services.OpportunityServiceImpl;

@RestController
@RequestMapping("/opportunities")
public class OpportunityController {

    private final OpportunityServiceImpl opportunityServiceImpl;

    public OpportunityController(OpportunityServiceImpl opportunityServiceImpl) {
        this.opportunityServiceImpl = opportunityServiceImpl;
    }

    @GetMapping("/")
    public List<Opportunity> getOpportunities() {
        return opportunityServiceImpl.getOpportunities();
    }

    @PostMapping("/")
    public  ResponseEntity<Opportunity> addOpportunity(@RequestBody Opportunity opportunity) {
        return ResponseEntity.ok(opportunityServiceImpl.addOpportunity(opportunity));
    }
}
