package com.org.back.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.back.dto.user.CompanyDto;
import com.org.back.exceptions.CompanyAlreadyExistException;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.interfaces.CompanyService;
import com.org.back.enums.CustomFieldTarget;
import com.org.back.mapper.CompanyMapper;
import com.org.back.models.Company;
import com.org.back.models.Contact;
import com.org.back.models.User;
import com.org.back.repositories.CompanyRepository;
import com.org.back.repositories.ContactRepository;
import com.org.back.repositories.UserRepository;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;
    private final CompanyMapper companyMapper;
    private final UserRepository userRepository;
    private final CustomFieldService customFieldService;

    public CompanyServiceImpl(CompanyRepository companyRepository, ContactRepository contactRepository,
            UserRepository userRepository,
            CompanyMapper companyMapper, CustomFieldService customFieldService) {
        this.companyRepository = companyRepository;
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
        this.companyMapper = companyMapper;
        this.customFieldService = customFieldService;
    }

    @Transactional(readOnly = true)
    @Override
    public CompanyDto getCompanyById(Long id) throws EntityNotFoundException {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Company not found with id: " + id));
        return companyMapper.toCompanyDTO(company);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CompanyDto> getContactCompanies(Long contactId) {
        return companyRepository.findAllByContacts_Id(contactId).stream()
                .map(companyMapper::toCompanyDTO).toList();
    }

    @Override
    public CompanyDto addCompany(Company company, Long userId)
            throws CompanyAlreadyExistException, EntityNotFoundException {
        if (companyRepository.existsByUser_IdAndName(userId, company.getName())) {
            throw new CompanyAlreadyExistException(
                    "Company with name " + company.getName() + " already exist.");
        }
        // TODO : Implement Company Sets to avoid all this ...
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        company.setUser(user);
        company.setCustomValues(customFieldService.validateValues(userId, CustomFieldTarget.COMPANY, company.getCustomValues()));
        if (company.getContacts() != null) {
            List<Contact> attachedContacts = new ArrayList<>();
            for (Contact contact : company.getContacts()) {
                attachedContacts.add(findOwnedContact(userId, contact.getId()));
            }
            attachedContacts.forEach(contact -> contact.setCompany(company));
            company.setContacts(attachedContacts);
        }

        Company savedCompany = companyRepository.save(company);
        return companyMapper.toCompanyDTO(savedCompany);
    }

    @Transactional()
    @Override
    public void updateCompany(Long userId, Company company) throws EntityNotFoundException {
        Company existingCompany = findOwnedCompany(userId, company.getId());

        existingCompany.setName(company.getName());
        existingCompany.setContacts(company.getContacts());
        existingCompany.setCity(company.getCity());
        existingCompany.setCountry(company.getCountry());
        existingCompany.setAddress(company.getAddress());
        existingCompany.setIndustry(company.getIndustry());
        existingCompany.setCustomValues(customFieldService.validateValues(
                existingCompany.getUser().getId(), CustomFieldTarget.COMPANY, company.getCustomValues()));
        if (company.getContacts() != null) {
            List<Contact> attachedContacts = company.getContacts().stream()
                    .map(c -> {
                        Contact contact = findOwnedContact(userId, c.getId());
                        contact.setCompany(existingCompany);
                        return contact;
                    })
                    .collect(Collectors.toList());
            existingCompany.setContacts(attachedContacts);
        }
        companyRepository.save(existingCompany);
    }

    // Its Contacts and Opportunities stay, without a Company: the database clears the link.
    @Override
    @Transactional
    public void deleteCompanyById(Long userId, Long id) throws EntityNotFoundException {
        companyRepository.delete(findOwnedCompany(userId, id));
    }

    // Another user's Company or Contact is reported as not found, so its existence does not leak.
    private Company findOwnedCompany(Long userId, Long companyId) throws EntityNotFoundException {
        return companyRepository.findByIdAndUser_Id(companyId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + companyId));
    }

    private Contact findOwnedContact(Long userId, Long contactId) throws EntityNotFoundException {
        return contactRepository.findByIdAndUser_Id(contactId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + contactId));
    }

    @Override
    public List<CompanyDto> getCompaniesByUserId(Long userId) {
        return companyRepository.findAllByUser_IdWithContacts(userId).stream()
                .map(companyMapper::toCompanyDTO).toList();
    }
}
