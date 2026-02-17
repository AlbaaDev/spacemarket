package com.org.back.Contact;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

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
public class ContactControllerTest {

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

    @Test
    @WithMockUser
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
        contact.setAdress("13 Rue de Genève");

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
}
