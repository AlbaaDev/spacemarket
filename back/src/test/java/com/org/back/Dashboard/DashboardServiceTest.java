package com.org.back.Dashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.org.back.dto.dashboard.DashboardSummaryDto;
import com.org.back.dto.dashboard.TimelinePointDto;
import com.org.back.enums.InteractionType;
import com.org.back.enums.OpportunityStatus;
import com.org.back.models.Contact;
import com.org.back.models.Interaction;
import com.org.back.models.Opportunity;
import com.org.back.models.User;
import com.org.back.services.DashboardService;

@DataJpaTest
@Import(DashboardService.class)
class DashboardServiceTest {

    private static final LocalDate FROM = LocalDate.of(2026, 3, 1);
    private static final LocalDate TO = LocalDate.of(2026, 3, 31);

    @Autowired
    DashboardService dashboardService;

    @Autowired
    TestEntityManager entityManager;

    private User user;
    private Contact alice;
    private Contact bob;

    @BeforeEach
    void setUp() {
        user = user("dashboard-owner@test.com");
        alice = contact(user, "Alice");
        bob = contact(user, "Bob");
    }

    @Test
    @DisplayName("Revenue counts only Won Opportunities closed inside the Period")
    void revenueCountsWonOpportunitiesClosedInPeriod() {
        opportunity(alice, 1_000, OpportunityStatus.WON, LocalDate.of(2026, 3, 1));
        opportunity(alice, 2_000, OpportunityStatus.WON, LocalDate.of(2026, 3, 31));
        opportunity(alice, 4_000, OpportunityStatus.WON, LocalDate.of(2026, 4, 1));
        opportunity(bob, 8_000, OpportunityStatus.LOST, LocalDate.of(2026, 3, 10));
        opportunity(bob, 16_000, OpportunityStatus.OPEN, null);

        DashboardSummaryDto summary = dashboardService.summary(user.getId(), FROM, TO, null, null);

        assertEquals(3_000, summary.current().revenue());
    }

    @Test
    @DisplayName("Contacts reached counts distinct Contacts, Interactions counts every exchange")
    void contactsReachedAreDistinct() {
        interaction(alice, LocalDate.of(2026, 3, 2));
        interaction(alice, LocalDate.of(2026, 3, 9));
        interaction(bob, LocalDate.of(2026, 3, 9));
        interaction(bob, LocalDate.of(2026, 2, 28));

        DashboardSummaryDto.Figures current = dashboardService.summary(user.getId(), FROM, TO, null, null).current();

        assertEquals(3, current.interactions());
        assertEquals(2, current.contactsReached());
    }

    @Test
    @DisplayName("The previous Period has the same length and ends the day before")
    void previousPeriodMirrorsCurrentOne() {
        interaction(bob, LocalDate.of(2026, 2, 28));
        interaction(bob, LocalDate.of(2026, 1, 29));

        DashboardSummaryDto.Figures previous = dashboardService.summary(user.getId(), FROM, TO, null, null).previous();

        assertEquals(LocalDate.of(2026, 1, 29), previous.from());
        assertEquals(LocalDate.of(2026, 2, 28), previous.to());
        assertEquals(2, previous.interactions());
    }

    @Test
    @DisplayName("An explicit comparison Period replaces the default one")
    void explicitComparisonPeriod() {
        interaction(bob, LocalDate.of(2026, 2, 1));
        interaction(bob, LocalDate.of(2026, 1, 31));

        DashboardSummaryDto.Figures previous = dashboardService
                .summary(user.getId(), FROM, TO, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28)).previous();

        assertEquals(LocalDate.of(2026, 2, 1), previous.from());
        assertEquals(1, previous.interactions());
    }

    @Test
    @DisplayName("Another user's data never shows up")
    void otherUsersAreIgnored() {
        User stranger = user("stranger@test.com");
        Contact strangersContact = contact(stranger, "Eve");
        opportunity(strangersContact, 5_000, OpportunityStatus.WON, LocalDate.of(2026, 3, 5));
        interaction(strangersContact, LocalDate.of(2026, 3, 5));

        DashboardSummaryDto.Figures current = dashboardService.summary(user.getId(), FROM, TO, null, null).current();

        assertEquals(0, current.revenue());
        assertEquals(0, current.interactions());
    }

    @Test
    @DisplayName("The timeline merges Revenue and Interactions per day")
    void timelineMergesDays() {
        opportunity(alice, 1_000, OpportunityStatus.WON, LocalDate.of(2026, 3, 9));
        interaction(alice, LocalDate.of(2026, 3, 9));
        interaction(bob, LocalDate.of(2026, 3, 12));

        List<TimelinePointDto> timeline = dashboardService.timeline(user.getId(), FROM, TO);

        assertEquals(List.of(
                new TimelinePointDto(LocalDate.of(2026, 3, 9), 1_000, 1),
                new TimelinePointDto(LocalDate.of(2026, 3, 12), 0, 1)), timeline);
    }

    private User user(String email) {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail(email);
        user.setPassword("password");
        return entityManager.persist(user);
    }

    private Contact contact(User owner, String firstName) {
        Contact contact = new Contact();
        contact.setFirstName(firstName);
        contact.setLastName("Test");
        contact.setUser(owner);
        return entityManager.persist(contact);
    }

    private void opportunity(Contact principalContact, long value, OpportunityStatus status, LocalDate closeDate) {
        Opportunity opportunity = new Opportunity();
        opportunity.setName("Deal");
        opportunity.setBusinessName("Business");
        opportunity.setValue(value);
        opportunity.setStatus(status);
        opportunity.setCloseDate(closeDate);
        opportunity.setPrincipalContact(principalContact);
        entityManager.persist(opportunity);
    }

    private void interaction(Contact contact, LocalDate occurredOn) {
        Interaction interaction = new Interaction();
        interaction.setType(InteractionType.CALL);
        interaction.setOccurredOn(occurredOn);
        interaction.setContact(contact);
        interaction.setUser(contact.getUser());
        entityManager.persist(interaction);
    }
}
