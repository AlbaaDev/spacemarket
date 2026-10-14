package com.org.back.Interaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.org.back.dto.interaction.InteractionDto;
import com.org.back.dto.interaction.InteractionRequest;
import com.org.back.dto.interaction.OpportunityInteractionRequest;
import com.org.back.enums.InteractionType;
import com.org.back.enums.Step;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.exceptions.InvalidInteractionException;
import com.org.back.models.Contact;
import com.org.back.models.Interaction;
import com.org.back.models.Opportunity;
import com.org.back.models.User;
import com.org.back.repositories.ContactRepository;
import com.org.back.repositories.InteractionRepository;
import com.org.back.repositories.OpportunityRepository;
import com.org.back.services.InteractionService;

@ExtendWith(MockitoExtension.class)
class InteractionServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long OPPORTUNITY_ID = 5L;

    @Mock
    InteractionRepository interactionRepository;
    @Mock
    ContactRepository contactRepository;
    @Mock
    OpportunityRepository opportunityRepository;

    private InteractionService service;
    private User user;
    private Contact contact;
    private Opportunity opportunity;

    @BeforeEach
    void setUp() {
        service = new InteractionService(interactionRepository, contactRepository, opportunityRepository);

        user = new User();
        user.setId(USER_ID);
        contact = new Contact();
        contact.setId(10L);
        contact.setUser(user);
        opportunity = new Opportunity();
        opportunity.setId(OPPORTUNITY_ID);
        opportunity.setUser(user);
        opportunity.getContacts().add(contact);
    }

    @Test
    @DisplayName("An Opportunity's Interactions come newest first")
    void opportunityInteractionsNewestFirst() throws EntityNotFoundException {
        when(opportunityRepository.findOwnedBy(OPPORTUNITY_ID, USER_ID)).thenReturn(Optional.of(opportunity));
        Interaction recent = interaction(2L, LocalDate.of(2026, 10, 5));
        Interaction older = interaction(1L, LocalDate.of(2026, 9, 1));
        when(interactionRepository.findAllByOpportunity_IdAndUser_IdOrderByOccurredOnDescIdDesc(OPPORTUNITY_ID, USER_ID))
                .thenReturn(List.of(recent, older));

        List<InteractionDto> interactions = service.getOpportunityInteractions(user, OPPORTUNITY_ID);

        assertEquals(List.of(2L, 1L), interactions.stream().map(InteractionDto::id).toList());
        assertEquals(OPPORTUNITY_ID, interactions.get(0).opportunityId());
    }

    @Test
    @DisplayName("Another user's Opportunity is not found")
    void foreignOpportunityIsRejected() {
        when(opportunityRepository.findOwnedBy(OPPORTUNITY_ID, USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getOpportunityInteractions(user, OPPORTUNITY_ID));
    }

    @Test
    @DisplayName("An Interaction logged from an Opportunity is linked to it and keeps its Step")
    void opportunityInteractionKeepsStep() throws EntityNotFoundException {
        when(contactRepository.findById(10L)).thenReturn(Optional.of(contact));
        when(opportunityRepository.findOwnedBy(OPPORTUNITY_ID, USER_ID)).thenReturn(Optional.of(opportunity));
        when(interactionRepository.save(any(Interaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InteractionDto logged = service.logOpportunityInteraction(user, OPPORTUNITY_ID, new OpportunityInteractionRequest(
                InteractionType.MEETING, LocalDate.of(2026, 10, 5), null, 10L, Step.CLIENT_TECHNICAL));

        assertEquals(OPPORTUNITY_ID, logged.opportunityId());
        assertEquals(10L, logged.contactId());
        assertEquals(Step.CLIENT_TECHNICAL, logged.step());
    }

    @Test
    @DisplayName("A Step without an Opportunity is rejected")
    void stepWithoutOpportunityIsRejected() {
        InteractionRequest request = new InteractionRequest(
                InteractionType.CALL, LocalDate.of(2026, 10, 5), null, null, Step.HR);

        assertThrows(InvalidInteractionException.class, () -> service.logInteraction(user, 10L, request));
        verifyNoInteractions(interactionRepository);
    }

    private Interaction interaction(Long id, LocalDate occurredOn) {
        Interaction interaction = new Interaction();
        interaction.setId(id);
        interaction.setType(InteractionType.CALL);
        interaction.setOccurredOn(occurredOn);
        interaction.setContact(contact);
        interaction.setOpportunity(opportunity);
        interaction.setUser(user);
        return interaction;
    }
}
