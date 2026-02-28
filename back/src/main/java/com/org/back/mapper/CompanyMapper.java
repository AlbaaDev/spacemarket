package com.org.back.mapper;

import com.org.back.dto.user.CompanyDto;
import com.org.back.models.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    
    @Mapping(target = "contacts", ignore = true)
    CompanyDto toCompanyDTO(Company company);
}
