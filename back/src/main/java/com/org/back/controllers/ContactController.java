package com.org.back.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.back.exceptions.ContactAlreadyExistException;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.models.Contact;
import com.org.back.services.ContactServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/contacts")
public class ContactController {
    private final ContactServiceImpl contactService;

    public ContactController(ContactServiceImpl contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContact(@PathVariable Long id) throws EntityNotFoundException {
       return ResponseEntity.ok(contactService.getContactById(id).get());
    }

    @PostMapping("/")
    public ResponseEntity<Contact> addContact(@Valid @RequestBody Contact contact) throws ContactAlreadyExistException {
        return ResponseEntity.ok(contactService.addContact(contact));
    }

    @PutMapping("/")
    public void editContact(@Valid @RequestBody Contact contact) throws EntityNotFoundException {
       contactService.updateContact(contact);
    }

    @GetMapping("/")
    public ResponseEntity<List<Contact>> getAllContacts() {
        return ResponseEntity.ok(contactService.getAllContacts());
    }

    @DeleteMapping("/{id}")
    public void deleteContact(@PathVariable Long id) {
        contactService.deleteContactById(id);
    }
}
