package com.org.back.Contact;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.List;
import java.util.Optional;

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
import com.org.back.models.Company;
import com.org.back.models.Contact;
import com.org.back.repositories.ContactRepository;
import com.org.back.security.jwt.JwtService;
import com.org.back.services.ContactServiceImpl;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

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

    // @BeforeAll
    // void setUp() {

    // Contact contact = new Contact();
    // contact.setId(1L);
    // contact.setFirstName("John");
    // contact.setLastName("Doe");
    // contact.setCompany(companyTest);
    // contact.setCountry("Switzerland");
    // contact.setCity("Geneva");
    // contact.setPhone("0637627383");
    // contact.setEmail("john.doe@gmail.com");
    // contact.setAddress("13 Rue de Genève");
    // }


    @Test
    @WithMockUser
    @DisplayName("Should return 201 and the newly added contact")
    void addContact_should_return_201_and_the_new_contact() throws Exception {

        // GIVEN
        Company companyTest = new Company();
        companyTest.setId(1L);
        companyTest.setName("companyTest");

        Contact contact = new Contact();
        contact.setId(1L);
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setCompany(companyTest);
        contact.setCountry("Switzerland");
        contact.setCity("Geneva");
        contact.setPhone("0637627383");
        contact.setEmail("john.doe@gmail.com");
        contact.setAddress("13 Rue de Genève");

        when(contactService.addContact(any(Contact.class)))
                .thenReturn(contact);

        // WHEN
        ResultActions response = mockMvc.perform(post("/contacts/")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contact)));

        response.andDo(print()); // Debug

        // THEN
        response
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.phone").value("0637627383"))
                .andExpect(jsonPath("$.email").value("john.doe@gmail.com"))
                .andExpect(jsonPath("$.address").value("13 Rue de Genève"));
    }


    @Test
    @WithMockUser
    @DisplayName("Should return 200 when user tries to get contact by id")
    void getContactById_should_return_contact() throws Exception {
        // GIVEN
        Company companyTest = new Company();
        companyTest.setId(1L);
        companyTest.setName("companyTest");

        Contact contact = new Contact();
        contact.setId(1L);
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setCompany(companyTest);
        contact.setCountry("Switzerland");
        contact.setCity("Geneva");
        contact.setPhone("0637627383");
        contact.setEmail("john.doe@gmail.com");
        contact.setAddress("13 Rue de Genève");

        Optional<Contact> optionalContact = Optional.of(contact);
        when(contactService.getContactById(1L)).thenReturn(optionalContact);

        // WHEN
        ResultActions response = mockMvc.perform(get("/contacts/1")
                .accept(MediaType.APPLICATION_JSON));

        // THEN
        response
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.phone").value("0637627383"));

    }

    @Test
    @WithMockUser
    @DisplayName("Should return 204 when user tries to get all contacts")
    void getAllContacts_should_return_all_contacts() throws Exception {
        // GIVEN
        Contact contact1 = new Contact();
        contact1.setId(1L);
        contact1.setFirstName("John");
        contact1.setLastName("Doe");

        Contact contact2 = new Contact();
        contact2.setId(2L);
        contact2.setFirstName("Jane");
        contact2.setLastName("Mary");

        List<Contact> userList = List.of(contact1, contact2);
        when(contactService.getAllContacts()).thenReturn(userList);

        // WHEN
        ResultActions response = mockMvc.perform(get("/contacts/").accept(MediaType.APPLICATION_JSON));

        // THEN
        response
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Mary"));
    }

    @Test
    @DisplayName("Should return 204 when user tries to assign company to contact")
    @WithMockUser
    void assigneCompanyToContact_should_return_204_status_code() throws Exception {
        // GIVEN
        Long contactId = 1L;
        Long companyId = 2L;
        doNothing().when(contactService).assigneCompanyToContact(contactId, companyId);

        // WHEN
        ResultActions response = mockMvc
                .perform(patch("/contacts/{contactId}/company/{companyId}", contactId, companyId)
                        .with(csrf()));

        // THEN
        response.andExpect(status().isNoContent());
        verify(contactService).assigneCompanyToContact(contactId, companyId);
    }

    @Test
    @DisplayName("Should return 403 when user tries to assign company to contact without csrf")
    @WithMockUser
    void assigneCompanyToContact_without_csrf_should_return_403_status_code() throws Exception {
        // GIVEN
        Long contactId = 1L;
        Long companyId = 2L;
        doNothing().when(contactService).assigneCompanyToContact(contactId, companyId);

        // WHEN
        ResultActions response = mockMvc
                .perform(patch("/contacts/{contactId}/company/{companyId}", contactId, companyId));

        // THEN
        response.andExpect(status().is(403));
    }
}
