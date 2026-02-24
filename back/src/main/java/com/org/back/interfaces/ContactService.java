package com.org.back.interfaces;

import java.util.List;

import com.org.back.dto.user.ContactDto;
import com.org.back.exceptions.ContactAlreadyExistException;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.models.Contact;

public interface ContactService {
    List<ContactDto> getUserContacts(Long userId);
    ContactDto addContact(Long userId, Contact contact) throws ContactAlreadyExistException, EntityNotFoundException;
    ContactDto getContactById(Long id) throws EntityNotFoundException;
    ContactDto updateContact(Contact contact) throws EntityNotFoundException, ContactAlreadyExistException;
    void deleteContactById(Long id) throws EntityNotFoundException;
    void addCompanyToContact(Long contactId, Long companyId) throws EntityNotFoundException;
}
