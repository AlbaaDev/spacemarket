package com.org.back.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.org.back.interfaces.OpportunityService;
import com.org.back.models.Opportunity;
import com.org.back.repositories.OpportunityRepository;

@Service
public class OpportunityServiceImpl implements OpportunityService {

    private final OpportunityRepository opportunityRepository;

    public OpportunityServiceImpl(OpportunityRepository opportunityRepository) {
        this.opportunityRepository = opportunityRepository;
    }

    @Override
    public List<Opportunity> getOpportunities() {
        return opportunityRepository.findAll();
    }

    @Override
    public Optional<Opportunity> getOpportunityById(Long opportunityId) {
        return opportunityRepository.findById(opportunityId);
    }

    @Override
    public Opportunity addOpportunity(Opportunity opportunity) {
        Opportunity newOpportunity = new Opportunity();
        opportunity.setName(opportunity.getName());
        opportunity.setContacts(opportunity.getContacts());
        opportunity.setBusinessName(opportunity.getBusinessName());
        opportunity.setPrincipalContact(opportunity.getPrincipalContact());
        
        return newOpportunity;
    }

    @Override
    public void updateOpportunity(Opportunity opportunity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateOpportunity'");
    }

    @Override
    public void deleteOpportunity(Opportunity opportunity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteOpportunity'");
    }

    
}
