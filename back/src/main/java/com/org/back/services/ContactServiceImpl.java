package com.org.back.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.org.back.dto.user.ContactDto;
import com.org.back.exceptions.ContactAlreadyExistException;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.interfaces.ContactService;
import com.org.back.mapper.ContactMapper;
import com.org.back.models.Contact;
import com.org.back.models.ContactEmail;
import com.org.back.models.ContactPhone;
import com.org.back.models.User;
import com.org.back.repositories.ContactEmailRepository;
import com.org.back.repositories.ContactPhoneRepository;
import com.org.back.repositories.ContactRepository;
import com.org.back.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final ContactEmailRepository contactEmailRepository;
    private final ContactPhoneRepository contactPhoneRepository;
    private final UserRepository userRepository;
    private final ContactMapper contactMapper;

    public ContactServiceImpl(ContactRepository contactRepository, ContactMapper contactMapper,
            ContactEmailRepository contactEmailRepository, ContactPhoneRepository contactPhoneRepository,
            UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.contactMapper = contactMapper;
        this.contactEmailRepository = contactEmailRepository;
        this.contactPhoneRepository = contactPhoneRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ContactDto> getUserContacts(Long userId) {
        List<ContactDto> contacts = contactRepository.findAllByUser_Id(userId).stream()
                .map(contactMapper::toContactDTO).toList();
        return contacts;
        // return
        // contactRepository.findAllByUser_Id(userId).stream().map(contactMapper::toContactDTO).toList();
    }

    @Override
    @Transactional
    public ContactDto addContact(Long userId, Contact contact)
            throws ContactAlreadyExistException, EntityNotFoundException {

        // TODO : Implement Sets to avoid all this ...
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        contact.setUser(user);
        if (contactEmailRepository.existsByContactFirstNameAndContactLastName(contact.getFirstName(),
                contact.getLastName())) {
            throw new ContactAlreadyExistException("Contact with firstName : " +
                    contact.getFirstName() + " and lastName : " + contact.getLastName()
                    + " already used by another contact.");
        }
        if (!(contact.getEmails().isEmpty())) {
            for (ContactEmail email : contact.getEmails()) {
                if (contactEmailRepository.existsByContactIdAndEmailAndType(userId, email.getEmail(),
                        email.getType())) {
                    throw new ContactAlreadyExistException(
                            "Email " + email.getEmail() + " already used by another contact.");
                }
                email.setContact(contact);
            }
        }
        if (contactPhoneRepository.existsByContactFirstNameAndContactLastName(contact.getFirstName(),
                contact.getLastName())) {
            throw new ContactAlreadyExistException("Contact with firstName : " +
                    contact.getFirstName() + " and lastName : " + contact.getLastName()
                    + " already used by another contact.");
        }
        if (!(contact.getPhones().isEmpty())) {
            for (ContactPhone phone : contact.getPhones()) {
                if (contactPhoneRepository.existsByContactIdAndPhoneAndType(
                        userId, phone.getPhone(), phone.getType())) {
                    throw new ContactAlreadyExistException(
                            "Phone " + phone.getPhone() + " already used by another contact.");
                }
                phone.setContact(contact);
            }
        }
        Contact savedContact = contactRepository.save(contact);
        return contactMapper.toContactDTO(savedContact);
    }

    @Override
    public ContactDto getContactById(Long id) throws EntityNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getContactById'");
    }

    @Override
    public ContactDto updateContact(Contact contactForm) throws ContactAlreadyExistException, EntityNotFoundException {

        Contact existingContact = contactRepository.findById(contactForm.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Contact not found with id: " + contactForm.getId()));

        Long userId = existingContact.getUser().getId();
        Long contactId = contactForm.getId();

        if (contactForm.getEmails() != null && !contactForm.getEmails().isEmpty()) {
            for (ContactEmail emailForm : contactForm.getEmails()) {
                if (contactEmailRepository.existsByUserIdAndEmailExcludingContact(
                        userId, emailForm.getEmail(), contactId)) {
                    throw new ContactAlreadyExistException(
                            "Email " + emailForm.getEmail() + " is already used by another contact");
                }
                emailForm.setContact(contactForm);
            }
        }

        if (contactForm.getPhones() != null && !contactForm.getPhones().isEmpty()) {
            for (ContactPhone phoneForm : contactForm.getPhones()) {
                if (contactPhoneRepository.existsByUserIdAndPhoneExcludingContact(
                        userId, phoneForm.getPhone(), contactId)) {
                    throw new ContactAlreadyExistException(
                            "Phone " + phoneForm.getPhone() + " is already used by another contact");
                }
                phoneForm.setContact(contactForm);
            }
        }
        contactForm.setUser(existingContact.getUser());
        Contact savedContact = contactRepository.save(contactForm);
        return contactMapper.toContactDTO(savedContact);
    }

    @Override
    public void deleteContactById(Long id) throws EntityNotFoundException {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + id));
        contactRepository.delete(contact);
    }

    @Override
    public void addCompanyToContact(Long contactId, Long companyId) throws EntityNotFoundException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addCompanyToContact'");
    }

}
