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

import com.org.back.dto.user.CompanyDto;
import com.org.back.exceptions.CompanyAlreadyExistException;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.models.ApiResponse;
import com.org.back.models.Company;
import com.org.back.models.ResponseUtil;
import com.org.back.models.User;
import com.org.back.services.CompanyServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/companies")
public class CompanyController {
    private final CompanyServiceImpl companyService;

    public CompanyController(CompanyServiceImpl companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<CompanyDto>> addCompany(@Valid @RequestBody Company company,
            HttpServletRequest request, @AuthenticationPrincipal User user) throws CompanyAlreadyExistException, EntityNotFoundException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseUtil.success(
                        HttpStatus.CREATED.value(),
                        "Company created successfully",
                        companyService.addCompany(company, user.getId()),
                        request.getRequestURI()));
    }

    @PatchMapping("/")
    public ResponseEntity<Void> editCompany(@Valid @RequestBody Company company) throws EntityNotFoundException {
        companyService.updateCompany(company);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<CompanyDto>>> getAllCompanies(@AuthenticationPrincipal User user, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseUtil.success(
                        HttpStatus.OK.value(),
                        "Companies retrieved successfully",
                        companyService.getCompaniesByUserId(user.getId()),
                        request.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompanyById(id);
        return ResponseEntity.noContent().build();
    }
}
