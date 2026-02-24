package com.org.back.Contact;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.org.back.enums.EmailType;
import com.org.back.enums.PhoneType;
import com.org.back.models.Contact;
import com.org.back.models.ContactEmail;
import com.org.back.models.ContactPhone;
import com.org.back.repositories.ContactRepository;

@DataJpaTest
class ContactRepositoryTest {

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    @DisplayName("Should return the newly added contact")
    void givenNewContact_whenSave_thenSuccess() {
        Contact contact = new Contact();
        contact.setFirstName("Jean");
        contact.setLastName("Dupont");
        contact.setCity("Geneva");
        contact.setCountry("Switzerland");

        ContactEmail contactEmail1 = new ContactEmail();
        contactEmail1.setContact(contact);
        contactEmail1.setEmail("contact1@work.com");
        contactEmail1.setPrimary(true);
        contactEmail1.setType(EmailType.WORK);

        ContactEmail contactEmail2 = new ContactEmail();
        contactEmail2.setContact(contact);
        contactEmail2.setEmail("contact1@home.com");
        contactEmail2.setPrimary(false);
        contactEmail2.setType(EmailType.HOME);

        ContactPhone contactPhone1 = new ContactPhone();
        contactPhone1.setContact(contact);
        contactPhone1.setPhone("04183746482");
        contactPhone1.setPrimary(true);
        contactPhone1.setType(PhoneType.WORK);

        ContactPhone contactPhone2 = new ContactPhone();
        contactPhone2.setContact(contact);
        contactPhone2.setPhone("0418372839");
        contactPhone2.setPrimary(false);
        contactPhone2.setType(PhoneType.HOME);

        List<ContactEmail> contactEmails = new ArrayList<>();
        List<ContactPhone> contactPhones = new ArrayList<>();

        Collections.addAll(contactEmails, contactEmail1, contactEmail2);
        Collections.addAll(contactPhones, contactPhone1, contactPhone2);

        contact.setEmails(contactEmails);   
        contact.setPhones(contactPhones);
        Contact addedContact = contactRepository.save(contact);
        assertEquals(entityManager.find(Contact.class, addedContact.getId()), addedContact);
    }
}
