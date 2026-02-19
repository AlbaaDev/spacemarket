package com.org.back.interfaces;

import java.util.List;
import java.util.Optional;

import com.org.back.exceptions.ContactAlreadyExistException;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.models.Contact;

public interface ContactService {
    List<Contact> getAllContacts();
    Contact addContact(Contact contact) throws ContactAlreadyExistException;
    Optional<Contact> getContactById(Long id);
    void updateContact(Contact contact) throws EntityNotFoundException;
    void deleteContactById(Long id);
    void assigneCompanyToContact(Long contactId, Long companyId) throws EntityNotFoundException;
}
