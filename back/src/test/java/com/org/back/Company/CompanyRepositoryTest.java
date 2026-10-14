package com.org.back.Company;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.org.back.models.Company;
import com.org.back.models.Contact;
import com.org.back.models.User;
import com.org.back.repositories.CompanyRepository;
import com.org.back.support.PostgresJpaTest;

@PostgresJpaTest
class CompanyRepositoryTest {

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    CompanyRepository companyRepository;

    @Test
    @DisplayName("Deleting a Company keeps its Contacts, without a Company")
    void deletingACompanyKeepsItsContacts() {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("owner@test.com");
        user.setPassword("password");
        entityManager.persist(user);

        Company company = new Company();
        company.setName("Acme");
        company.setUser(user);
        entityManager.persist(company);

        Contact contact = new Contact();
        contact.setFirstName("Alice");
        contact.setLastName("Martin");
        contact.setUser(user);
        contact.setCompany(company);
        Long contactId = entityManager.persist(contact).getId();
        entityManager.flush();
        entityManager.clear();

        companyRepository.deleteById(company.getId());
        entityManager.flush();
        entityManager.clear();

        assertNull(entityManager.find(Contact.class, contactId).getCompany());
    }
}
