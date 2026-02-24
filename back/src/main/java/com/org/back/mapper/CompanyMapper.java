package com.org.back.mapper;

import com.org.back.dto.user.CompanyDto;
import com.org.back.models.Company;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    CompanyDto toCompanyDTO(Company company);
}
