package com.org.back.CustomField;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.org.back.dto.customfield.CustomFieldDto;
import com.org.back.dto.customfield.CustomFieldRequest;
import com.org.back.enums.CustomFieldTarget;
import com.org.back.enums.CustomFieldType;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.exceptions.InvalidCustomFieldException;
import com.org.back.models.Contact;
import com.org.back.models.User;
import com.org.back.services.CustomFieldService;

@DataJpaTest
@Import(CustomFieldService.class)
class CustomFieldServiceTest {

    @Autowired
    CustomFieldService service;

    @Autowired
    TestEntityManager entityManager;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("custom-fields@test.com");
        user.setPassword("password");
        user = entityManager.persist(user);
    }

    @Test
    @DisplayName("Values are checked and normalised against each field's type")
    void valuesAreNormalisedByType() {
        CustomFieldDto budget = field("Budget", CustomFieldType.NUMBER);
        CustomFieldDto since = field("Client since", CustomFieldType.DATE);
        CustomFieldDto vip = field("VIP", CustomFieldType.CHECKBOX);
        CustomFieldDto source = choice("Source", "Referral", "Website");
        CustomFieldDto notes = field("Notes", CustomFieldType.TEXT);

        Map<String, Object> submitted = new HashMap<>();
        submitted.put(key(budget), "1500.5");
        submitted.put(key(since), "2026-01-15");
        submitted.put(key(vip), false);
        submitted.put(key(source), "Website");
        submitted.put(key(notes), "   ");

        Map<String, Object> clean = service.validateValues(user.getId(), CustomFieldTarget.CONTACT, submitted);

        assertEquals(1500.5, clean.get(key(budget)));
        assertEquals("2026-01-15", clean.get(key(since)));
        assertEquals("Website", clean.get(key(source)));
        assertFalse(clean.containsKey(key(vip)), "an unchecked box is stored as no value");
        assertFalse(clean.containsKey(key(notes)), "a blank text is stored as no value");
    }

    @Test
    @DisplayName("A value of the wrong type, an unknown choice or an unknown field is refused")
    void invalidValuesAreRefused() {
        CustomFieldDto budget = field("Budget", CustomFieldType.NUMBER);
        CustomFieldDto source = choice("Source", "Referral");

        assertThrows(InvalidCustomFieldException.class,
                () -> service.validateValues(user.getId(), CustomFieldTarget.CONTACT, Map.of(key(budget), "a lot")));
        assertThrows(InvalidCustomFieldException.class,
                () -> service.validateValues(user.getId(), CustomFieldTarget.CONTACT, Map.of(key(source), "Cold call")));
        assertThrows(InvalidCustomFieldException.class,
                () -> service.validateValues(user.getId(), CustomFieldTarget.CONTACT, Map.of("999", "x")));
        // A Contact field does not exist on Companies.
        assertThrows(InvalidCustomFieldException.class,
                () -> service.validateValues(user.getId(), CustomFieldTarget.COMPANY, Map.of(key(budget), 1)));
    }

    @Test
    @DisplayName("Two fields of the same table cannot share a name")
    void namesAreUniquePerTable() {
        field("Source", CustomFieldType.TEXT);

        assertThrows(InvalidCustomFieldException.class, () -> field("source", CustomFieldType.NUMBER));
    }

    @Test
    @DisplayName("Deleting a field removes its values from every record")
    void deletingFieldRemovesValues() throws EntityNotFoundException {
        CustomFieldDto budget = field("Budget", CustomFieldType.NUMBER);
        CustomFieldDto vip = field("VIP", CustomFieldType.CHECKBOX);
        Contact contact = contact(Map.of(key(budget), 100, key(vip), true));

        service.deleteField(user.getId(), budget.id());
        entityManager.flush();
        entityManager.clear();

        Map<String, Object> values = entityManager.find(Contact.class, contact.getId()).getCustomValues();
        assertFalse(values.containsKey(key(budget)));
        assertTrue(values.containsKey(key(vip)));
    }

    @Test
    @DisplayName("Removing a choice clears it from the records that had it")
    void removingChoiceClearsValues() throws EntityNotFoundException {
        CustomFieldDto source = choice("Source", "Referral", "Website");
        Contact referred = contact(Map.of(key(source), "Referral"));
        Contact web = contact(Map.of(key(source), "Website"));

        service.updateField(user.getId(), source.id(),
                new CustomFieldRequest("Source", CustomFieldTarget.CONTACT, CustomFieldType.SINGLE_CHOICE, List.of("Website")));
        entityManager.flush();
        entityManager.clear();

        assertFalse(entityManager.find(Contact.class, referred.getId()).getCustomValues().containsKey(key(source)));
        assertEquals("Website", entityManager.find(Contact.class, web.getId()).getCustomValues().get(key(source)));
    }

    @Test
    @DisplayName("The type of a field cannot change")
    void typeIsFixed() {
        CustomFieldDto budget = field("Budget", CustomFieldType.NUMBER);

        assertThrows(InvalidCustomFieldException.class, () -> service.updateField(user.getId(), budget.id(),
                new CustomFieldRequest("Budget", CustomFieldTarget.CONTACT, CustomFieldType.TEXT, null)));
    }

    private CustomFieldDto field(String name, CustomFieldType type) {
        return service.createField(user, new CustomFieldRequest(name, CustomFieldTarget.CONTACT, type, null));
    }

    private CustomFieldDto choice(String name, String... options) {
        return service.createField(user,
                new CustomFieldRequest(name, CustomFieldTarget.CONTACT, CustomFieldType.SINGLE_CHOICE, List.of(options)));
    }

    private Contact contact(Map<String, Object> values) {
        Contact contact = new Contact();
        contact.setFirstName("Alice");
        contact.setLastName("Test");
        contact.setUser(user);
        contact.setCustomValues(new HashMap<>(values));
        return entityManager.persist(contact);
    }

    private static String key(CustomFieldDto field) {
        return field.id().toString();
    }
}
