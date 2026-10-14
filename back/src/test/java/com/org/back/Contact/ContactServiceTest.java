package com.org.back.Contact;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.mapper.ContactMapper;
import com.org.back.models.Company;
import com.org.back.models.Contact;
import com.org.back.repositories.CompanyRepository;
import com.org.back.repositories.ContactEmailRepository;
import com.org.back.repositories.ContactPhoneRepository;
import com.org.back.repositories.ContactRepository;
import com.org.back.repositories.InteractionRepository;
import com.org.back.repositories.UserRepository;
import com.org.back.services.ContactServiceImpl;
import com.org.back.services.CustomFieldService;

// Another user's Contact is not found, whatever the request does with it.
@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;
    private static final Long CONTACT_ID = 10L;

    @Mock
    ContactRepository contactRepository;
    @Mock
    ContactMapper contactMapper;
    @Mock
    ContactEmailRepository contactEmailRepository;
    @Mock
    ContactPhoneRepository contactPhoneRepository;
    @Mock
    CompanyRepository companyRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    InteractionRepository interactionRepository;
    @Mock
    CustomFieldService customFieldService;

    private ContactServiceImpl service;
    private Contact contact;

    @BeforeEach
    void setUp() {
        service = new ContactServiceImpl(contactRepository, contactMapper, contactEmailRepository, contactPhoneRepository,
                companyRepository, userRepository, interactionRepository, customFieldService);
        contact = new Contact();
        contact.setId(CONTACT_ID);
    }

    @Test
    @DisplayName("Another user's Contact cannot be read")
    void foreignContactIsNotRead() {
        when(contactRepository.findByIdAndUser_Id(CONTACT_ID, OTHER_USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getContactById(OTHER_USER_ID, CONTACT_ID));
    }

    @Test
    @DisplayName("Another user's Contact cannot be updated")
    void foreignContactIsNotUpdated() {
        when(contactRepository.findByIdAndUser_Id(CONTACT_ID, OTHER_USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateContact(OTHER_USER_ID, contact));
        verify(contactRepository, never()).save(any());
    }

    @Test
    @DisplayName("A Contact cannot be moved to another user's Company")
    void foreignCompanyIsNotAssigned() {
        Company foreignCompany = new Company();
        foreignCompany.setId(20L);
        contact.setCompany(foreignCompany);
        when(contactRepository.findByIdAndUser_Id(CONTACT_ID, USER_ID)).thenReturn(Optional.of(contact));
        when(companyRepository.findByIdAndUser_Id(20L, USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateContact(USER_ID, contact));
        verify(contactRepository, never()).save(any());
    }

    @Test
    @DisplayName("Another user's Contact cannot be deleted, nor its Interactions")
    void foreignContactIsNotDeleted() {
        when(contactRepository.findByIdAndUser_Id(CONTACT_ID, OTHER_USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deleteContactById(OTHER_USER_ID, CONTACT_ID));
        verify(interactionRepository, never()).deleteAllByContactId(anyLong());
        verify(contactRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deleting one's own Contact deletes its Interactions too")
    void ownContactIsDeleted() throws EntityNotFoundException {
        when(contactRepository.findByIdAndUser_Id(CONTACT_ID, USER_ID)).thenReturn(Optional.of(contact));

        service.deleteContactById(USER_ID, CONTACT_ID);

        verify(interactionRepository).deleteAllByContactId(CONTACT_ID);
        verify(contactRepository).delete(contact);
    }
}
