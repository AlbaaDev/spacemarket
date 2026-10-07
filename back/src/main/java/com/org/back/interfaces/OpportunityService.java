package com.org.back.interfaces;

import java.util.List;

import com.org.back.dto.opportunity.OpportunityDto;
import com.org.back.dto.opportunity.OpportunityRequest;
import com.org.back.exceptions.EntityNotFoundException;

public interface OpportunityService {
    List<OpportunityDto> getOpportunities(Long userId);
    OpportunityDto addOpportunity(Long userId, OpportunityRequest request) throws EntityNotFoundException;
    OpportunityDto updateOpportunity(Long userId, Long opportunityId, OpportunityRequest request) throws EntityNotFoundException;
    void deleteOpportunity(Long userId, Long opportunityId) throws EntityNotFoundException;
}
