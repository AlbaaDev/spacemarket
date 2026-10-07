package com.org.back.Opportunity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.org.back.dto.opportunity.OpportunityDto;
import com.org.back.dto.opportunity.OpportunityRequest;
import com.org.back.enums.OpportunityStatus;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.models.Contact;
import com.org.back.models.Opportunity;
import com.org.back.models.User;
import com.org.back.repositories.ContactRepository;
import com.org.back.repositories.InteractionRepository;
import com.org.back.repositories.OpportunityRepository;
import com.org.back.services.OpportunityServiceImpl;

@ExtendWith(MockitoExtension.class)
class OpportunityServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 7);
    private static final Long USER_ID = 1L;
    private static final Long CONTACT_ID = 10L;

    @Mock
    OpportunityRepository opportunityRepository;
    @Mock
    ContactRepository contactRepository;
    @Mock
    InteractionRepository interactionRepository;

    private OpportunityServiceImpl service;
    private Contact contact;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(TODAY.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        service = new OpportunityServiceImpl(opportunityRepository, contactRepository, interactionRepository, clock);

        User user = new User();
        user.setId(USER_ID);
        contact = new Contact();
        contact.setId(CONTACT_ID);
        contact.setFirstName("Alice");
        contact.setLastName("Martin");
        contact.setUser(user);
    }

    @Test
    @DisplayName("A new Opportunity without status is Open with no Close date")
    void newOpportunityIsOpen() throws EntityNotFoundException {
        givenOwnedContact();
        givenSaveEchoes();

        OpportunityDto created = service.addOpportunity(USER_ID, request(null, null));

        assertEquals(OpportunityStatus.OPEN, created.status());
        assertNull(created.closeDate());
    }

    @Test
    @DisplayName("Closing without a date sets the Close date to today")
    void closingDefaultsCloseDateToToday() throws EntityNotFoundException {
        givenOwnedContact();
        givenSaveEchoes();

        OpportunityDto created = service.addOpportunity(USER_ID, request(OpportunityStatus.WON, null));

        assertEquals(TODAY, created.closeDate());
    }

    @Test
    @DisplayName("An explicit Close date is kept, for deals won in the past")
    void explicitCloseDateIsKept() throws EntityNotFoundException {
        givenOwnedContact();
        givenSaveEchoes();
        LocalDate twoWeeksAgo = TODAY.minusWeeks(2);

        OpportunityDto created = service.addOpportunity(USER_ID, request(OpportunityStatus.WON, twoWeeksAgo));

        assertEquals(twoWeeksAgo, created.closeDate());
    }

    @Test
    @DisplayName("Reopening a Won Opportunity clears its Close date")
    void reopeningClearsCloseDate() throws EntityNotFoundException {
        Opportunity won = new Opportunity();
        won.setId(5L);
        won.setStatus(OpportunityStatus.WON);
        won.setCloseDate(TODAY.minusDays(3));
        won.setPrincipalContact(contact);
        when(opportunityRepository.findOwnedBy(5L, USER_ID)).thenReturn(Optional.of(won));
        givenOwnedContact();
        givenSaveEchoes();

        OpportunityDto updated = service.updateOpportunity(USER_ID, 5L, request(OpportunityStatus.OPEN, TODAY));

        assertEquals(OpportunityStatus.OPEN, updated.status());
        assertNull(updated.closeDate());
    }

    @Test
    @DisplayName("A Contact owned by someone else cannot be the principal Contact")
    void foreignContactIsRejected() {
        when(contactRepository.findById(CONTACT_ID)).thenReturn(Optional.of(contact));

        assertThrows(EntityNotFoundException.class,
                () -> service.addOpportunity(99L, request(null, null)));
    }

    private OpportunityRequest request(OpportunityStatus status, LocalDate closeDate) {
        return new OpportunityRequest("Website", "Acme", 12_500L, status, closeDate, CONTACT_ID);
    }

    private void givenOwnedContact() {
        when(contactRepository.findById(CONTACT_ID)).thenReturn(Optional.of(contact));
    }

    private void givenSaveEchoes() {
        when(opportunityRepository.save(any(Opportunity.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }
}
