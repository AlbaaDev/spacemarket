package com.org.back.Opportunity;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.org.back.models.Company;
import com.org.back.models.Contact;
import com.org.back.models.Opportunity;
import com.org.back.models.User;
import com.org.back.repositories.CompanyRepository;
import com.org.back.repositories.ContactRepository;
import com.org.back.repositories.OpportunityRepository;
import com.org.back.support.PostgresJpaTest;

// An Opportunity outlives its Contacts and its Company (docs/adr/0003-opportunity-belongs-to-the-user.md).
@PostgresJpaTest
class OpportunityRepositoryTest {

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    OpportunityRepository opportunityRepository;
    @Autowired
    ContactRepository contactRepository;
    @Autowired
    CompanyRepository companyRepository;

    private User user;
    private Contact contact;
    private Company company;
    private Long opportunityId;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("owner@test.com");
        user.setPassword("password");
        entityManager.persist(user);

        contact = new Contact();
        contact.setFirstName("Alice");
        contact.setLastName("Martin");
        contact.setUser(user);
        entityManager.persist(contact);

        company = new Company();
        company.setName("Acme");
        company.setUser(user);
        entityManager.persist(company);

        Opportunity opportunity = new Opportunity();
        opportunity.setName("Website");
        opportunity.setValue(12_500L);
        opportunity.setUser(user);
        opportunity.setCompany(company);
        opportunity.getContacts().add(contact);
        opportunityId = entityManager.persist(opportunity).getId();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Deleting a Contact keeps its Opportunities, without that Contact")
    void deletingAContactKeepsTheOpportunity() {
        contactRepository.deleteById(contact.getId());
        entityManager.flush();
        entityManager.clear();

        Opportunity opportunity = opportunityRepository.findOwnedBy(opportunityId, user.getId()).orElseThrow();
        assertTrue(opportunity.getContacts().isEmpty());
    }

    @Test
    @DisplayName("Deleting a Company keeps its Opportunities, without a Company")
    void deletingACompanyKeepsTheOpportunity() {
        companyRepository.deleteById(company.getId());
        entityManager.flush();
        entityManager.clear();

        Opportunity opportunity = opportunityRepository.findOwnedBy(opportunityId, user.getId()).orElseThrow();
        assertNull(opportunity.getCompany());
    }
}
