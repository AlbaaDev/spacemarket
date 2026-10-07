package com.org.back.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.back.dto.interaction.InteractionDto;
import com.org.back.dto.interaction.InteractionRequest;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.models.Contact;
import com.org.back.models.Interaction;
import com.org.back.models.Opportunity;
import com.org.back.models.User;
import com.org.back.repositories.ContactRepository;
import com.org.back.repositories.InteractionRepository;
import com.org.back.repositories.OpportunityRepository;

@Service
public class InteractionService {

    private final InteractionRepository interactionRepository;
    private final ContactRepository contactRepository;
    private final OpportunityRepository opportunityRepository;

    public InteractionService(InteractionRepository interactionRepository, ContactRepository contactRepository,
            OpportunityRepository opportunityRepository) {
        this.interactionRepository = interactionRepository;
        this.contactRepository = contactRepository;
        this.opportunityRepository = opportunityRepository;
    }

    @Transactional(readOnly = true)
    public List<InteractionDto> getContactInteractions(User user, Long contactId) throws EntityNotFoundException {
        findOwnedContact(user, contactId);
        return interactionRepository.findAllByContact_IdAndUser_IdOrderByOccurredOnDescIdDesc(contactId, user.getId())
                .stream().map(InteractionService::toDto).toList();
    }

    @Transactional
    public InteractionDto logInteraction(User user, Long contactId, InteractionRequest request)
            throws EntityNotFoundException {
        Interaction interaction = new Interaction();
        interaction.setContact(findOwnedContact(user, contactId));
        interaction.setUser(user);
        interaction.setType(request.type());
        interaction.setOccurredOn(request.occurredOn());
        interaction.setNote(request.note() == null || request.note().isBlank() ? null : request.note().trim());
        if (request.opportunityId() != null) {
            Opportunity opportunity = opportunityRepository.findOwnedBy(request.opportunityId(), user.getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Opportunity not found with id: " + request.opportunityId()));
            interaction.setOpportunity(opportunity);
        }
        return toDto(interactionRepository.save(interaction));
    }

    @Transactional
    public void deleteInteraction(User user, Long interactionId) throws EntityNotFoundException {
        Interaction interaction = interactionRepository.findByIdAndUser_Id(interactionId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Interaction not found with id: " + interactionId));
        interactionRepository.delete(interaction);
    }

    private Contact findOwnedContact(User user, Long contactId) throws EntityNotFoundException {
        return contactRepository.findById(contactId)
                .filter(contact -> contact.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + contactId));
    }

    private static InteractionDto toDto(Interaction interaction) {
        return new InteractionDto(
                interaction.getId(),
                interaction.getType(),
                interaction.getOccurredOn(),
                interaction.getNote(),
                interaction.getContact().getId(),
                interaction.getOpportunity() == null ? null : interaction.getOpportunity().getId());
    }
}
