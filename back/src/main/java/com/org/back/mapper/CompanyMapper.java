package com.org.back.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.org.back.dto.user.CompanyDto;
import com.org.back.dto.user.ContactDto;
import com.org.back.models.Company;
import com.org.back.models.Contact;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    
    @Mapping(source = "contacts", target = "contacts", qualifiedByName = "ignoreCompany")
    CompanyDto toCompanyDTO(Company company);
    
    @Named("ignoreCompany")
    @Mapping(target = "company", ignore = true)
    ContactDto toContactDTOIgnoreCompany(Contact contact);
}
