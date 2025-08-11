package com.org.back.interfaces;

import java.util.List;
import java.util.Optional;

import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.models.Contact;

public interface ContactService {
    List<Contact> getAllContacts();
    Contact addContact(Contact contact);
    Optional<Contact> getContactById(Long id);
    void updateContact(Long id, Contact contact) throws EntityNotFoundException;
    void deleteContactById(Long id);
}
