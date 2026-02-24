package com.org.back.Contact;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.back.controllers.ContactController;
import com.org.back.dto.user.CompanyDto;
import com.org.back.dto.user.ContactDto;
import com.org.back.dto.user.ContactEmailDto;
import com.org.back.dto.user.ContactPhoneDto;
import com.org.back.enums.EmailType;
import com.org.back.enums.PhoneType;
import com.org.back.models.Contact;
import com.org.back.models.User;
import com.org.back.repositories.ContactRepository;
import com.org.back.security.jwt.JwtService;
import com.org.back.services.ContactServiceImpl;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

    private static User mockUser;
    private static ContactDto contactDto1;
    private static CompanyDto companyDto1;
    private static List<ContactEmailDto> contact1EmailsDto;
    private static List<ContactPhoneDto> contact1PhonesDto;

    private static ContactDto contactDto2;
    private static CompanyDto companyDto2;
    private static List<ContactEmailDto> contact2EmailsDto;
    private static List<ContactPhoneDto> contact2PhonesDto;

    private static List<ContactDto> contactsDto;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContactServiceImpl contactService;

    @MockitoBean
    private ContactRepository contactRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthenticationProvider authenticationProvider;

    @BeforeAll
    static void setup() {
        mockUser = new User();
        mockUser.setId(1L);

        companyDto1 = new CompanyDto(1L, "companyTest1", "Suisse", "Genève", "13 Rue de Genève");
        contact1EmailsDto = new ArrayList<>();
        contact1EmailsDto.add(new ContactEmailDto(1L, "john.doe@work.com", EmailType.WORK, true));
        contact1EmailsDto.add(new ContactEmailDto(2L, "john.doe@home.com", EmailType.HOME, false));
        contact1PhonesDto = new ArrayList<>();
        contact1PhonesDto.add(new ContactPhoneDto(1L, "0412345678", PhoneType.WORK, true));
        contact1PhonesDto.add(new ContactPhoneDto(2L, "0637166248", PhoneType.HOME, false));
        companyDto2 = new CompanyDto(1L, "companyTest2", "France", "Paris", "13 rue de Paris");
        contactDto1 = new ContactDto(
                1L,
                "John",
                "Doe",
                contact1PhonesDto,
                contact1EmailsDto,
                companyDto1,
                "Genève",
                "13 Rue de Genève",
                "Suisse");

        contact2EmailsDto = new ArrayList<>();
        contact2EmailsDto.add(new ContactEmailDto(1L, "jean.dupont@work.com", EmailType.WORK, true));
        contact2EmailsDto.add(new ContactEmailDto(2L, "jean.dupont@home.com", EmailType.HOME, false));

        contact2PhonesDto = new ArrayList<>();
        contact2PhonesDto.add(new ContactPhoneDto(1L, "04123456789", PhoneType.WORK, true));
        contact2PhonesDto.add(new ContactPhoneDto(2L, "0636163562", PhoneType.HOME, false));

        contactDto2 = new ContactDto(
                1L,
                "Jean",
                "Dupont",
                contact2PhonesDto,
                contact2EmailsDto,
                companyDto2,
                "Lausanne",
                "13 Rue de Lausanne",
                "Suisse");

        contactsDto = List.of(contactDto1, contactDto2);
    }

    @Test
    @DisplayName("Should return 201 when user tries to add a contact")
    void addContact_should_return_contact() throws Exception {
        // GIVEN
        when(contactService.addContact(eq(1L), any(Contact.class))).thenReturn(contactDto1);

        // WHEN
        ResultActions response = mockMvc.perform(post("/contacts/")
                .with(csrf())
                .with(user(mockUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contactDto1)));

        // THEN
        response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.emails[0].email").value("john.doe@work.com"))
                .andExpect(jsonPath("$.emails[1].email").value("john.doe@home.com"))
                .andExpect(jsonPath("$.phones[0].phone").value("0412345678"))
                .andExpect(jsonPath("$.phones[1].phone").value("0637166248"))
                .andExpect(jsonPath("$.data.address").value("13 Rue de Genève"))
                .andExpect(jsonPath("$.data.city").value("Genève"))
                .andExpect(jsonPath("$.data.country").value("Suisse"));
    }

    @Test
    @DisplayName("Should return 200 when user tries to get contact by id")
    void getContactById_should_return_contact() throws Exception {
        // GIVEN
        when(contactService.getContactById(1L)).thenReturn(contactDto1);

        // WHEN
        ResultActions response = mockMvc.perform(get("/contacts/1")
                .with(user(mockUser))
                .accept(MediaType.APPLICATION_JSON));

        // THEN
        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.emails[0].email").value("john.doe@work.com"))
                .andExpect(jsonPath("$.data.emails[1].email").value("john.doe@home.com"))
                .andExpect(jsonPath("$.data.phones[0].phone").value("0412345678"))
                .andExpect(jsonPath("$.data.phones[1].phone").value("0637166248"))
                .andExpect(jsonPath("$.data.address").value("13 Rue de Genève"))
                .andExpect(jsonPath("$.data.city").value("Genève"))
                .andExpect(jsonPath("$.data.country").value("Suisse"));
    }

    @Test
    @DisplayName("Should return 200 when user tries to get all contacts")
    void getAllContacts_should_return_all_contacts() throws Exception {
        // GIVEN
        when(contactService.getUserContacts(1L)).thenReturn(contactsDto);

        // WHEN
        ResultActions response = mockMvc.perform(get("/contacts/")
                .with(user(mockUser))
                .accept(MediaType.APPLICATION_JSON));

        // THEN
        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].firstName").value("John"))
                .andExpect(jsonPath("$.data[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.data[0].emails[0].email").value("john.doe@work.com"))
                .andExpect(jsonPath("$.data[0].emails[1].email").value("john.doe@home.com"))
                .andExpect(jsonPath("$.data[0].phones[0].phone").value("0412345678"))
                .andExpect(jsonPath("$.data[0].phones[1].phone").value("0637166248"))
                .andExpect(jsonPath("$.data[0].address").value("13 Rue de Genève"))
                .andExpect(jsonPath("$.data[0].city").value("Genève"))
                .andExpect(jsonPath("$.data[0].country").value("Suisse"))

                .andExpect(jsonPath("$.data[1].firstName").value("Jean"))
                .andExpect(jsonPath("$.data[1].lastName").value("Dupont"))
                .andExpect(jsonPath("$.data[1].emails[0].email").value("jean.dupont@work.com"))
                .andExpect(jsonPath("$.data[1].emails[1].email").value("jean.dupont@home.com"))
                .andExpect(jsonPath("$.data[1].phones[0].phone").value("04123456789"))
                .andExpect(jsonPath("$.data[1].phones[1].phone").value("0636163562"))
                .andExpect(jsonPath("$.data[1].address").value("13 Rue de Lausanne"))
                .andExpect(jsonPath("$.data[1].city").value("Lausanne"))
                .andExpect(jsonPath("$.data[1].country").value("Suisse"));
    }

    @Test
    @DisplayName("Should return 204 when user tries to assign company to contact")
    @WithMockUser
    void assigneCompanyToContact_should_return_204_status_code() throws Exception {
        // GIVEN
        Long contactId = 1L;
        Long companyId = 2L;
        doNothing().when(contactService).addCompanyToContact(contactId, companyId);

        // WHEN
        ResultActions response = mockMvc
                .perform(patch("/contacts/{contactId}/company/{companyId}", contactId, companyId)
                        .with(csrf()));

        // THEN
        response.andExpect(status().isNoContent());
        verify(contactService).addCompanyToContact(contactId, companyId);
    }

    @Test
    @DisplayName("Should return 403 when user tries to assign company to contact without csrf")
    @WithMockUser
    void assigneCompanyToContact_without_csrf_should_return_403_status_code() throws Exception {
        // GIVEN
        Long contactId = 1L;
        Long companyId = 2L;
        doNothing().when(contactService).addCompanyToContact(contactId, companyId);

        // WHEN
        ResultActions response = mockMvc
                .perform(patch("/contacts/{contactId}/company/{companyId}", contactId, companyId));

        // THEN
        response.andExpect(status().is(403));
    }
}
