package com.org.back.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.org.back.dto.interaction.InteractionDto;
import com.org.back.dto.interaction.InteractionRequest;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.models.ApiResponse;
import com.org.back.models.ResponseUtil;
import com.org.back.models.User;
import com.org.back.services.InteractionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @GetMapping("/contacts/{contactId}/interactions")
    public ResponseEntity<ApiResponse<List<InteractionDto>>> getContactInteractions(@PathVariable Long contactId,
            @AuthenticationPrincipal User user, HttpServletRequest request) throws EntityNotFoundException {
        return ResponseEntity.ok(ResponseUtil.success(
                HttpStatus.OK.value(),
                "Success",
                interactionService.getContactInteractions(user, contactId),
                request.getRequestURI()));
    }

    @PostMapping("/contacts/{contactId}/interactions")
    public ResponseEntity<ApiResponse<InteractionDto>> logInteraction(@PathVariable Long contactId,
            @Valid @RequestBody InteractionRequest body, @AuthenticationPrincipal User user,
            HttpServletRequest request) throws EntityNotFoundException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseUtil.success(
                        HttpStatus.CREATED.value(),
                        "Interaction logged",
                        interactionService.logInteraction(user, contactId, body),
                        request.getRequestURI()));
    }

    @DeleteMapping("/interactions/{id}")
    public ResponseEntity<Void> deleteInteraction(@PathVariable Long id, @AuthenticationPrincipal User user)
            throws EntityNotFoundException {
        interactionService.deleteInteraction(user, id);
        return ResponseEntity.noContent().build();
    }
}
