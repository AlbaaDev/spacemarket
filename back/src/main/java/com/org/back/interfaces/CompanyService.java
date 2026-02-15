package com.org.back.interfaces;

import java.util.List;
import java.util.Optional;

import com.org.back.exceptions.CompanyAlreadyExistException;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.models.Company;

public interface CompanyService {
    List<Company> getAllCompanies();
    Company addCompany(Company company) throws CompanyAlreadyExistException;
    Optional<Company> getCompanyById(Long id);
    void updateCompany(Company company) throws EntityNotFoundException;
    void deleteCompanyById(Long id);
}
