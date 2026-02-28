package com.org.back.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.back.dto.user.CompanyDto;
import com.org.back.exceptions.CompanyAlreadyExistException;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.interfaces.CompanyService;
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

    public CompanyServiceImpl(CompanyRepository companyRepository, ContactRepository contactRepository,
            UserRepository userRepository,
            CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
        this.companyMapper = companyMapper;
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
        if (companyRepository.findByName(company.getName()).isPresent()) {
            throw new CompanyAlreadyExistException(
                    "Company with name " + company.getName() + " already exist.");
        }
        // TODO : Implement Company Sets to avoid all this ...
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        company.setUser(user);
        if (company.getContacts() != null) {
            List<Contact> attachedContacts = company.getContacts().stream()
                    .map((Contact contact) -> {
                        try {
                            return contactRepository.findById(contact.getId())
                                    .orElseThrow(() -> new EntityNotFoundException(
                                            "Contact not found with id: " + contact.getId()));
                        } catch (EntityNotFoundException e) {
                            e.printStackTrace();
                        }
                        return contact;
                    }) 
                    .collect(Collectors.toList());

            attachedContacts.forEach(contact -> contact.setCompany(company));
            company.setContacts(attachedContacts);
        }

        Company savedCompany = companyRepository.save(company);
        return companyMapper.toCompanyDTO(savedCompany);
    }

    @Transactional()
    @Override
    public void updateCompany(Company company) throws EntityNotFoundException {
        Company existingCompany = companyRepository.findById(company.getId())
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + company.getId()));

        existingCompany.setName(company.getName());
        existingCompany.setContacts(company.getContacts());
        existingCompany.setCity(company.getCity());
        existingCompany.setCountry(company.getCountry());
        existingCompany.setAddress(company.getAddress());
        existingCompany.setIndustry(company.getIndustry());
        if (company.getContacts() != null) {
            List<Contact> attachedContacts = company.getContacts().stream()
                    .map(c -> {
                        Contact contact = contactRepository.findById(c.getId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                        "Contact not found with id: " + c.getId()));
                        contact.setCompany(existingCompany);
                        return contact;
                    })
                    .collect(Collectors.toList());
            existingCompany.setContacts(attachedContacts);
        }
        companyRepository.save(existingCompany);
    }

    @Override
    public void deleteCompanyById(Long id) {
        companyRepository.deleteById(id);
    }

    @Override
    public List<CompanyDto> getCompaniesByUserId(Long userId) {
        List<CompanyDto> list = companyRepository.findAllByUser_IdWithContacts(userId).stream()
                .map(companyMapper::toCompanyDTO).toList();
        return list;
    }
}
