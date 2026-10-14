package com.org.back.Contact;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.org.back.enums.EmailType;
import com.org.back.enums.PhoneType;
import com.org.back.models.Company;
import com.org.back.models.Contact;
import com.org.back.models.ContactEmail;
import com.org.back.models.ContactPhone;
import com.org.back.models.User;
import com.org.back.repositories.CompanyRepository;
import com.org.back.repositories.ContactEmailRepository;
import com.org.back.repositories.ContactPhoneRepository;
import com.org.back.repositories.ContactRepository;
import com.org.back.support.PostgresJpaTest;

// Duplicates are looked for within one account: another account may use the same names.
@PostgresJpaTest
class DuplicateCheckRepositoryTest {

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    ContactRepository contactRepository;
    @Autowired
    ContactEmailRepository contactEmailRepository;
    @Autowired
    ContactPhoneRepository contactPhoneRepository;

    private User owner;
    private User other;

    @BeforeEach
    void setUp() {
        owner = user("owner@test.com");
        other = user("other@test.com");

        Company company = new Company();
        company.setName("Google");
        company.setUser(owner);
        entityManager.persist(company);

        Contact contact = new Contact();
        contact.setFirstName("Jean");
        contact.setLastName("Dupont");
        contact.setUser(owner);
        ContactEmail email = new ContactEmail();
        email.setEmail("jean@dupont.fr");
        email.setType(EmailType.WORK);
        email.setContact(contact);
        ContactPhone phone = new ContactPhone();
        phone.setPhone("0102030405");
        phone.setType(PhoneType.WORK);
        phone.setContact(contact);
        contact.setEmails(List.of(email));
        contact.setPhones(List.of(phone));
        entityManager.persist(contact);
        entityManager.flush();
    }

    @Test
    @DisplayName("A Company name is taken in its account only")
    void companyName() {
        assertTrue(companyRepository.existsByUser_IdAndName(owner.getId(), "Google"));
        assertFalse(companyRepository.existsByUser_IdAndName(other.getId(), "Google"));
    }

    @Test
    @DisplayName("A Contact's name, email and phone are taken in its account only")
    void contactNameEmailAndPhone() {
        assertTrue(contactRepository.existsByUser_IdAndFirstNameAndLastName(owner.getId(), "Jean", "Dupont"));
        assertFalse(contactRepository.existsByUser_IdAndFirstNameAndLastName(other.getId(), "Jean", "Dupont"));
        assertTrue(contactEmailRepository.existsByContact_User_IdAndEmail(owner.getId(), "jean@dupont.fr"));
        assertFalse(contactEmailRepository.existsByContact_User_IdAndEmail(other.getId(), "jean@dupont.fr"));
        assertTrue(contactPhoneRepository.existsByContact_User_IdAndPhone(owner.getId(), "0102030405"));
        assertFalse(contactPhoneRepository.existsByContact_User_IdAndPhone(other.getId(), "0102030405"));
    }

    private User user(String email) {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail(email);
        user.setPassword("password");
        return entityManager.persist(user);
    }
}
