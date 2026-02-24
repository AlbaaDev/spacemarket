package com.org.back.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.back.dto.user.ContactDto;
import com.org.back.exceptions.ContactAlreadyExistException;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.interfaces.ContactService;
import com.org.back.models.ApiResponse;
import com.org.back.models.Contact;
import com.org.back.models.ResponseUtil;
import com.org.back.models.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/contacts")
public class ContactController {
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactDto>> getContactById(@PathVariable Long id, HttpServletRequest request,
            @AuthenticationPrincipal User user) throws EntityNotFoundException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseUtil.success(
                        HttpStatus.OK.value(),
                        "Success",
                        contactService.getContactById(user.getId()),
                        request.getRequestURI()));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<ContactDto>> addContact(@Valid @RequestBody Contact contact,
            HttpServletRequest request, @AuthenticationPrincipal User user)
            throws ContactAlreadyExistException, EntityNotFoundException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseUtil.success(
                        HttpStatus.CREATED.value(),
                        "Contact created successfully",
                        contactService.addContact(user.getId(), contact),
                        request.getRequestURI()));
    }

    @PatchMapping("/")
    public ResponseEntity<Void> updateContact(@Valid @RequestBody Contact contact)
            throws EntityNotFoundException, ContactAlreadyExistException {
        contactService.updateContact(contact);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<ContactDto>>> getUserContacts(@AuthenticationPrincipal User user,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                ResponseUtil.success(
                        HttpStatus.OK.value(),
                        "Success. ",
                        contactService.getUserContacts(user.getId()),
                        request.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) throws EntityNotFoundException {
        contactService.deleteContactById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{contactId}/company/{companyId}")
    public ResponseEntity<Void> addCompanyToContact(
            @PathVariable Long contactId,
            @PathVariable Long companyId) throws EntityNotFoundException {

        contactService.addCompanyToContact(contactId, companyId);
        return ResponseEntity.noContent().build();
    }
}
