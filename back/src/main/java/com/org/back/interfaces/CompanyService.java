package com.org.back.interfaces;

import java.util.List;

import com.org.back.dto.user.CompanyDto;
import com.org.back.exceptions.CompanyAlreadyExistException;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.models.Company;

public interface CompanyService {
    List<CompanyDto> getContactCompanies(Long companyId);
    CompanyDto addCompany(Company company, Long userId) throws CompanyAlreadyExistException, EntityNotFoundException;
    CompanyDto getCompanyById(Long id) throws EntityNotFoundException;
    void updateCompany(Company company) throws EntityNotFoundException;
    void deleteCompanyById(Long id);
    List<CompanyDto> getCompaniesByUserId(Long userId);
}
