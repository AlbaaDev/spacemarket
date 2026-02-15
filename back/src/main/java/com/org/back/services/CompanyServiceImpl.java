package com.org.back.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.back.exceptions.CompanyAlreadyExistException;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.interfaces.CompanyService;
import com.org.back.models.Company;
import com.org.back.repositories.CompanyRepository;

@Service
public class CompanyServiceImpl implements CompanyService {
    CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Company> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public Company addCompany(Company company) throws CompanyAlreadyExistException {
        if (companyRepository.findByName(company.getName()).isPresent()) {
            throw new CompanyAlreadyExistException(
                    "Company with name " + company.getName() + " already exist.");
        }
        return companyRepository.save(company);
    }

    @Transactional()
    @Override
    public void updateCompany(Company company) throws EntityNotFoundException {

        Company editedCompany = companyRepository.findById(company.getId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + company.getId()));
        editedCompany.setName(company.getName());
        companyRepository.save(editedCompany);
    }

    @Override
    public void deleteCompanyById(Long id) {
        companyRepository.deleteById(id);
    }
}
