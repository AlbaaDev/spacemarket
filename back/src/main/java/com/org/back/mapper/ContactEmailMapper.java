package com.org.back.mapper;

import org.mapstruct.Mapper;

import com.org.back.dto.user.ContactEmailDto;
import com.org.back.models.ContactEmail;

@Mapper(componentModel = "spring")
public interface ContactEmailMapper {
    ContactEmailDto toContactEmailDTO(ContactEmail contactEmail);
}
