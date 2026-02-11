package com.org.back.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.back.exceptions.ContactAlreadyExistException;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.interfaces.ContactService;
import com.org.back.models.Contact;
import com.org.back.repositories.ContactRepository;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    @Override
    public Contact addContact(Contact contact) throws ContactAlreadyExistException {
        if (contactRepository.findByEmail(contact.getEmail()).isPresent()) {
            throw new ContactAlreadyExistException(
                    "Contact email is already in use. Please use a different email adress.");
        }
        if (contactRepository.findByPhone(contact.getPhone()).isPresent()) {
            throw new ContactAlreadyExistException(
                    "Contact phone number is already in use. Please use a different phone number.");
        }
        return contactRepository.save(contact);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Contact> getContactById(Long id) {
        return contactRepository.findById(id);
    }

    @Transactional()
    @Override
    public void updateContact(Contact contact) throws EntityNotFoundException {
        
        Contact editedcontact = contactRepository.findById(contact.getId())
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + contact.getId()));
        editedcontact.setFirstName(contact.getFirstName());
        editedcontact.setLastName(contact.getLastName());
        editedcontact.setAdress(contact.getAdress());
        editedcontact.setCity(contact.getCity());
        editedcontact.setBirthDate(contact.getBirthDate());
        editedcontact.setCountry(contact.getCountry());
        editedcontact.setEmail(contact.getEmail());
        editedcontact.setPhone(contact.getPhone());
        if (contact.getOpportunities() != null && !(contact.getOpportunities().isEmpty())) {
            editedcontact.getOpportunities().addAll(contact.getOpportunities());
        }
    
        contactRepository.save(editedcontact);
    }

    @Override
    public void deleteContactById(Long id) {
        contactRepository.deleteById(id);
    }
}
