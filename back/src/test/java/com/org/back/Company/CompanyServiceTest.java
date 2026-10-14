package com.org.back.Company;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.mapper.CompanyMapper;
import com.org.back.models.Company;
import com.org.back.models.Contact;
import com.org.back.models.User;
import com.org.back.repositories.CompanyRepository;
import com.org.back.repositories.ContactRepository;
import com.org.back.repositories.UserRepository;
import com.org.back.services.CompanyServiceImpl;
import com.org.back.services.CustomFieldService;

// Another user's Company, or Contact, is not found, whatever the request does with it.
@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;
    private static final Long COMPANY_ID = 20L;
    private static final Long CONTACT_ID = 10L;

    @Mock
    CompanyRepository companyRepository;
    @Mock
    ContactRepository contactRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CompanyMapper companyMapper;
    @Mock
    CustomFieldService customFieldService;

    private CompanyServiceImpl service;
    private Company company;

    @BeforeEach
    void setUp() {
        service = new CompanyServiceImpl(companyRepository, contactRepository, userRepository, companyMapper, customFieldService);
        company = new Company();
        company.setId(COMPANY_ID);
        company.setName("Acme");
    }

    @Test
    @DisplayName("Deleting one's own Company deletes it")
    void ownCompanyIsDeleted() throws EntityNotFoundException {
        when(companyRepository.findByIdAndUser_Id(COMPANY_ID, USER_ID)).thenReturn(Optional.of(company));

        service.deleteCompanyById(USER_ID, COMPANY_ID);

        verify(companyRepository).delete(company);
    }

    @Test
    @DisplayName("Another user's Company cannot be deleted")
    void foreignCompanyIsNotDeleted() {
        when(companyRepository.findByIdAndUser_Id(COMPANY_ID, OTHER_USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.deleteCompanyById(OTHER_USER_ID, COMPANY_ID));
        verify(companyRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Another user's Company cannot be updated")
    void foreignCompanyIsNotUpdated() {
        when(companyRepository.findByIdAndUser_Id(COMPANY_ID, OTHER_USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateCompany(OTHER_USER_ID, company));
        verify(companyRepository, never()).save(any());
    }

    @Test
    @DisplayName("A new Company cannot take another user's Contact")
    void foreignContactIsNotAttached() {
        Contact foreignContact = new Contact();
        foreignContact.setId(CONTACT_ID);
        company.setContacts(List.of(foreignContact));
        User user = new User();
        user.setId(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(contactRepository.findByIdAndUser_Id(CONTACT_ID, USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.addCompany(company, USER_ID));
        verify(companyRepository, never()).save(any());
    }
}
