package com.org.back.services;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.back.dto.opportunity.OpportunityDto;
import com.org.back.dto.opportunity.OpportunityRequest;
import com.org.back.enums.CustomFieldTarget;
import com.org.back.enums.OpportunityStatus;
import com.org.back.enums.Step;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.interfaces.OpportunityService;
import com.org.back.models.Company;
import com.org.back.models.Contact;
import com.org.back.models.Opportunity;
import com.org.back.repositories.CompanyRepository;
import com.org.back.repositories.ContactRepository;
import com.org.back.repositories.InteractionRepository;
import com.org.back.repositories.OpportunityRepository;
import com.org.back.repositories.UserRepository;

@Service
public class OpportunityServiceImpl implements OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final InteractionRepository interactionRepository;
    private final CustomFieldService customFieldService;
    private final Clock clock;

    public OpportunityServiceImpl(OpportunityRepository opportunityRepository, ContactRepository contactRepository,
            CompanyRepository companyRepository, UserRepository userRepository,
            InteractionRepository interactionRepository, CustomFieldService customFieldService, Clock clock) {
        this.opportunityRepository = opportunityRepository;
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.interactionRepository = interactionRepository;
        this.customFieldService = customFieldService;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpportunityDto> getOpportunities(Long userId) {
        Map<Long, Step> steps = new HashMap<>();
        for (Object[] row : interactionRepository.findOpportunitySteps(userId)) {
            steps.merge((Long) row[0], (Step) row[1], (a, b) -> mostAdvanced(List.of(a, b)));
        }
        return opportunityRepository.findAllOwnedBy(userId).stream()
                .map(opportunity -> toDto(opportunity, steps.get(opportunity.getId())))
                .toList();
    }

    @Override
    @Transactional
    public OpportunityDto addOpportunity(Long userId, OpportunityRequest request) throws EntityNotFoundException {
        Opportunity opportunity = new Opportunity();
        opportunity.setUser(userRepository.getReferenceById(userId));
        apply(userId, opportunity, request);
        return toDto(opportunityRepository.save(opportunity), null);
    }

    @Override
    @Transactional
    public OpportunityDto updateOpportunity(Long userId, Long opportunityId, OpportunityRequest request)
            throws EntityNotFoundException {
        Opportunity opportunity = findOwned(userId, opportunityId);
        apply(userId, opportunity, request);
        return toDto(opportunityRepository.save(opportunity), mostAdvanced(interactionRepository.findSteps(opportunityId)));
    }

    @Override
    @Transactional
    public void deleteOpportunity(Long userId, Long opportunityId) throws EntityNotFoundException {
        Opportunity opportunity = findOwned(userId, opportunityId);
        interactionRepository.detachOpportunity(opportunityId);
        opportunityRepository.delete(opportunity);
    }

    private void apply(Long userId, Opportunity opportunity, OpportunityRequest request) throws EntityNotFoundException {
        opportunity.setName(request.name());
        opportunity.setCompany(request.companyId() == null ? null : findOwnedCompany(userId, request.companyId()));
        opportunity.setValue(request.value());
        opportunity.getContacts().clear();
        opportunity.getContacts().addAll(findOwnedContacts(userId, request.contactIds()));
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

    private Company findOwnedCompany(Long userId, Long companyId) throws EntityNotFoundException {
        return companyRepository.findById(companyId)
                .filter(company -> company.getUser().getId().equals(userId))
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + companyId));
    }

    private Set<Contact> findOwnedContacts(Long userId, List<Long> contactIds) throws EntityNotFoundException {
        Set<Contact> contacts = new HashSet<>();
        if (contactIds == null) {
            return contacts;
        }
        for (Long contactId : new HashSet<>(contactIds)) {
            contacts.add(contactRepository.findById(contactId)
                    .filter(contact -> contact.getUser().getId().equals(userId))
                    .orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + contactId)));
        }
        return contacts;
    }

    private Opportunity findOwned(Long userId, Long opportunityId) throws EntityNotFoundException {
        return opportunityRepository.findOwnedBy(opportunityId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Opportunity not found with id: " + opportunityId));
    }

    // An Opportunity's Step is the most advanced Step among its Interactions, whatever their dates.
    private static Step mostAdvanced(Collection<Step> steps) {
        return steps.stream().max(Comparator.naturalOrder()).orElse(null);
    }

    private static OpportunityDto toDto(Opportunity opportunity, Step step) {
        Company company = opportunity.getCompany();
        List<OpportunityDto.ContactRef> contacts = opportunity.getContacts().stream()
                .sorted(Comparator.comparing(Contact::getLastName).thenComparing(Contact::getFirstName))
                .map(contact -> new OpportunityDto.ContactRef(contact.getId(), contact.getFirstName(), contact.getLastName()))
                .toList();
        return new OpportunityDto(
                opportunity.getId(),
                opportunity.getName(),
                company == null ? null : new OpportunityDto.CompanyRef(company.getId(), company.getName()),
                opportunity.getValue(),
                opportunity.getStatus(),
                opportunity.getCloseDate(),
                contacts,
                opportunity.getCustomValues(),
                step);
    }
}
