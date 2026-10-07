package com.org.back.services;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.back.dto.opportunity.OpportunityDto;
import com.org.back.dto.opportunity.OpportunityRequest;
import com.org.back.enums.CustomFieldTarget;
import com.org.back.enums.OpportunityStatus;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.interfaces.OpportunityService;
import com.org.back.models.Contact;
import com.org.back.models.Opportunity;
import com.org.back.repositories.ContactRepository;
import com.org.back.repositories.InteractionRepository;
import com.org.back.repositories.OpportunityRepository;

@Service
public class OpportunityServiceImpl implements OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final ContactRepository contactRepository;
    private final InteractionRepository interactionRepository;
    private final CustomFieldService customFieldService;
    private final Clock clock;

    public OpportunityServiceImpl(OpportunityRepository opportunityRepository, ContactRepository contactRepository,
            InteractionRepository interactionRepository, CustomFieldService customFieldService, Clock clock) {
        this.opportunityRepository = opportunityRepository;
        this.contactRepository = contactRepository;
        this.interactionRepository = interactionRepository;
        this.customFieldService = customFieldService;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpportunityDto> getOpportunities(Long userId) {
        return opportunityRepository.findAllOwnedBy(userId).stream().map(OpportunityServiceImpl::toDto).toList();
    }

    @Override
    @Transactional
    public OpportunityDto addOpportunity(Long userId, OpportunityRequest request) throws EntityNotFoundException {
        Opportunity opportunity = new Opportunity();
        apply(userId, opportunity, request);
        return toDto(opportunityRepository.save(opportunity));
    }

    @Override
    @Transactional
    public OpportunityDto updateOpportunity(Long userId, Long opportunityId, OpportunityRequest request)
            throws EntityNotFoundException {
        Opportunity opportunity = findOwned(userId, opportunityId);
        apply(userId, opportunity, request);
        return toDto(opportunityRepository.save(opportunity));
    }

    @Override
    @Transactional
    public void deleteOpportunity(Long userId, Long opportunityId) throws EntityNotFoundException {
        Opportunity opportunity = findOwned(userId, opportunityId);
        interactionRepository.detachOpportunity(opportunityId);
        opportunityRepository.delete(opportunity);
    }

    private void apply(Long userId, Opportunity opportunity, OpportunityRequest request) throws EntityNotFoundException {
        Contact principalContact = contactRepository.findById(request.principalContactId())
                .filter(contact -> contact.getUser().getId().equals(userId))
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + request.principalContactId()));

        opportunity.setName(request.name());
        opportunity.setBusinessName(request.businessName());
        opportunity.setValue(request.value());
        opportunity.setPrincipalContact(principalContact);
        opportunity.setCustomValues(customFieldService.validateValues(userId, CustomFieldTarget.OPPORTUNITY, request.customValues()));

        OpportunityStatus status = request.status() == null ? OpportunityStatus.OPEN : request.status();
        opportunity.setStatus(status);
        // Close date: today by default when an Opportunity is closed, editable, cleared on reopening.
        if (status == OpportunityStatus.OPEN) {
            opportunity.setCloseDate(null);
        } else if (request.closeDate() != null) {
            opportunity.setCloseDate(request.closeDate());
        } else if (opportunity.getCloseDate() == null) {
            opportunity.setCloseDate(LocalDate.now(clock));
        }
    }

    private Opportunity findOwned(Long userId, Long opportunityId) throws EntityNotFoundException {
        return opportunityRepository.findOwnedBy(opportunityId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Opportunity not found with id: " + opportunityId));
    }

    private static OpportunityDto toDto(Opportunity opportunity) {
        Contact contact = opportunity.getPrincipalContact();
        return new OpportunityDto(
                opportunity.getId(),
                opportunity.getName(),
                opportunity.getBusinessName(),
                opportunity.getValue(),
                opportunity.getStatus(),
                opportunity.getCloseDate(),
                new OpportunityDto.PrincipalContact(contact.getId(), contact.getFirstName(), contact.getLastName()),
                opportunity.getCustomValues());
    }
}
