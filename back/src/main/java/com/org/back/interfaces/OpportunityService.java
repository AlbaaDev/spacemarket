package com.org.back.interfaces;

import java.util.List;
import java.util.Optional;

import com.org.back.models.Opportunity;

public interface OpportunityService {
    Optional<Opportunity> getOpportunityById(Long opportunityId);
    List<Opportunity> getOpportunities();
    Opportunity addOpportunity(Opportunity opportunity);
    void updateOpportunity(Opportunity opportunity);
    void deleteOpportunity(Opportunity opportunity);
}
