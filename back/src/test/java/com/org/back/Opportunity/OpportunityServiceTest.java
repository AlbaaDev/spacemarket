package com.org.back.Opportunity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
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
import com.org.back.enums.Step;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.models.Company;
import com.org.back.models.Contact;
import com.org.back.models.Opportunity;
import com.org.back.models.User;
import com.org.back.repositories.CompanyRepository;
import com.org.back.repositories.ContactRepository;
import com.org.back.repositories.InteractionRepository;
import com.org.back.repositories.OpportunityRepository;
import com.org.back.repositories.UserRepository;
import com.org.back.services.CustomFieldService;
import com.org.back.services.OpportunityServiceImpl;

@ExtendWith(MockitoExtension.class)
class OpportunityServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 7);
    private static final Long USER_ID = 1L;
    private static final Long CONTACT_ID = 10L;
    private static final Long OTHER_CONTACT_ID = 11L;
    private static final Long COMPANY_ID = 20L;

    @Mock
    OpportunityRepository opportunityRepository;
    @Mock
    ContactRepository contactRepository;
    @Mock
    CompanyRepository companyRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    InteractionRepository interactionRepository;
    @Mock
    CustomFieldService customFieldService;

    private OpportunityServiceImpl service;
    private User user;
    private Contact contact;
    private Contact otherContact;
    private Company company;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(TODAY.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        service = new OpportunityServiceImpl(opportunityRepository, contactRepository, companyRepository, userRepository,
                interactionRepository, customFieldService, clock);

        user = new User();
        user.setId(USER_ID);
        contact = contact(CONTACT_ID, "Alice", "Martin");
        otherContact = contact(OTHER_CONTACT_ID, "Bruno", "Dupont");
        company = new Company();
        company.setId(COMPANY_ID);
        company.setName("Acme");
        company.setUser(user);
    }

    @Test
    @DisplayName("A new Opportunity without status is Open with no Close date")
    void newOpportunityIsOpen() throws EntityNotFoundException {
        givenSaveEchoes();

        OpportunityDto created = service.addOpportunity(USER_ID, request(null, null));

        assertEquals(OpportunityStatus.OPEN, created.status());
        assertNull(created.closeDate());
    }

    @Test
    @DisplayName("Closing without a date sets the Close date to today")
    void closingDefaultsCloseDateToToday() throws EntityNotFoundException {
        givenSaveEchoes();

        OpportunityDto created = service.addOpportunity(USER_ID, request(OpportunityStatus.WON, null));

        assertEquals(TODAY, created.closeDate());
    }

    @Test
    @DisplayName("An explicit Close date is kept, for deals won in the past")
    void explicitCloseDateIsKept() throws EntityNotFoundException {
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
        won.setUser(user);
        when(opportunityRepository.findOwnedBy(5L, USER_ID)).thenReturn(Optional.of(won));
        givenSaveEchoes();

        OpportunityDto updated = service.updateOpportunity(USER_ID, 5L, request(OpportunityStatus.OPEN, TODAY));

        assertEquals(OpportunityStatus.OPEN, updated.status());
        assertNull(updated.closeDate());
    }

    @Test
    @DisplayName("An Opportunity's Step is its most advanced Step, not its latest one")
    void stepIsTheMostAdvanced() {
        Opportunity interviewing = new Opportunity();
        interviewing.setId(5L);
        interviewing.setUser(user);
        Opportunity untouched = new Opportunity();
        untouched.setId(6L);
        untouched.setUser(user);
        when(opportunityRepository.findAllOwnedBy(USER_ID)).thenReturn(List.of(interviewing, untouched));
        // The HR exchange comes after the Client interview, yet the Client interview is further along.
        when(interactionRepository.findOpportunitySteps(USER_ID)).thenReturn(List.of(
                new Object[] { 5L, Step.CLIENT },
                new Object[] { 5L, Step.HR }));

        List<OpportunityDto> opportunities = service.getOpportunities(USER_ID);

        assertEquals(Step.CLIENT, opportunities.get(0).step());
        assertNull(opportunities.get(1).step());
    }

    @Test
    @DisplayName("An Opportunity can be created without a Company or Contacts")
    void companyAndContactsAreOptional() throws EntityNotFoundException {
        givenSaveEchoes();

        OpportunityDto created = service.addOpportunity(USER_ID, request(null, null));

        assertNull(created.company());
        assertTrue(created.contacts().isEmpty());
    }

    @Test
    @DisplayName("An Opportunity is created with its Company and several Contacts, sorted by name")
    void companyAndSeveralContacts() throws EntityNotFoundException {
        when(companyRepository.findById(COMPANY_ID)).thenReturn(Optional.of(company));
        when(contactRepository.findById(CONTACT_ID)).thenReturn(Optional.of(contact));
        when(contactRepository.findById(OTHER_CONTACT_ID)).thenReturn(Optional.of(otherContact));
        givenSaveEchoes();

        OpportunityDto created = service.addOpportunity(USER_ID,
                assigning(COMPANY_ID, List.of(CONTACT_ID, OTHER_CONTACT_ID, CONTACT_ID)));

        assertEquals(new OpportunityDto.CompanyRef(COMPANY_ID, "Acme"), created.company());
        assertEquals(List.of("Dupont", "Martin"), created.contacts().stream().map(OpportunityDto.ContactRef::lastName).toList());
    }

    @Test
    @DisplayName("Updating replaces the Contacts, so one can be removed")
    void updateReplacesContacts() throws EntityNotFoundException {
        Opportunity existing = new Opportunity();
        existing.setId(5L);
        existing.setUser(user);
        existing.getContacts().add(contact);
        existing.getContacts().add(otherContact);
        when(opportunityRepository.findOwnedBy(5L, USER_ID)).thenReturn(Optional.of(existing));
        when(contactRepository.findById(OTHER_CONTACT_ID)).thenReturn(Optional.of(otherContact));
        givenSaveEchoes();

        OpportunityDto updated = service.updateOpportunity(USER_ID, 5L, assigning(null, List.of(OTHER_CONTACT_ID)));

        assertEquals(List.of(OTHER_CONTACT_ID), updated.contacts().stream().map(OpportunityDto.ContactRef::id).toList());
    }

    @Test
    @DisplayName("A Contact owned by someone else cannot be assigned")
    void foreignContactIsRejected() {
        when(contactRepository.findById(CONTACT_ID)).thenReturn(Optional.of(contact));

        assertThrows(EntityNotFoundException.class,
                () -> service.addOpportunity(99L, assigning(null, List.of(CONTACT_ID))));
    }

    @Test
    @DisplayName("A Company owned by someone else cannot be assigned")
    void foreignCompanyIsRejected() {
        when(companyRepository.findById(COMPANY_ID)).thenReturn(Optional.of(company));

        assertThrows(EntityNotFoundException.class,
                () -> service.addOpportunity(99L, assigning(COMPANY_ID, null)));
    }

    private OpportunityRequest request(OpportunityStatus status, LocalDate closeDate) {
        return new OpportunityRequest("Website", null, 12_500L, status, closeDate, null, null);
    }

    private OpportunityRequest assigning(Long companyId, List<Long> contactIds) {
        return new OpportunityRequest("Website", companyId, 12_500L, null, null, contactIds, null);
    }

    private Contact contact(Long id, String firstName, String lastName) {
        Contact created = new Contact();
        created.setId(id);
        created.setFirstName(firstName);
        created.setLastName(lastName);
        created.setUser(user);
        return created;
    }

    private void givenSaveEchoes() {
        when(opportunityRepository.save(any(Opportunity.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }
}
