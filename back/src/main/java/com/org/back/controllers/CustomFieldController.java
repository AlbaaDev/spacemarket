package com.org.back.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.back.dto.customfield.CustomFieldDto;
import com.org.back.dto.customfield.CustomFieldRequest;
import com.org.back.enums.CustomFieldTarget;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.models.ApiResponse;
import com.org.back.models.ResponseUtil;
import com.org.back.models.User;
import com.org.back.services.CustomFieldService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/custom-fields")
public class CustomFieldController {

    private final CustomFieldService customFieldService;

    public CustomFieldController(CustomFieldService customFieldService) {
        this.customFieldService = customFieldService;
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<CustomFieldDto>>> getFields(@RequestParam CustomFieldTarget target,
            @AuthenticationPrincipal User user, HttpServletRequest request) {
        return ResponseEntity.ok(ResponseUtil.success(
                HttpStatus.OK.value(), "Success", customFieldService.getFields(user.getId(), target),
                request.getRequestURI()));
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<CustomFieldDto>> createField(@Valid @RequestBody CustomFieldRequest body,
            @AuthenticationPrincipal User user, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseUtil.success(
                        HttpStatus.CREATED.value(), "Field created", customFieldService.createField(user, body),
                        request.getRequestURI()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomFieldDto>> updateField(@PathVariable Long id,
            @Valid @RequestBody CustomFieldRequest body, @AuthenticationPrincipal User user,
            HttpServletRequest request) throws EntityNotFoundException {
        return ResponseEntity.ok(ResponseUtil.success(
                HttpStatus.OK.value(), "Field updated", customFieldService.updateField(user.getId(), id, body),
                request.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteField(@PathVariable Long id, @AuthenticationPrincipal User user)
            throws EntityNotFoundException {
        customFieldService.deleteField(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
