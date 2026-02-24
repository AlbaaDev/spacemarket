package com.org.back.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.org.back.dto.user.ContactDto;
import com.org.back.models.Contact;

@Mapper(componentModel = "spring", uses = {CompanyMapper.class, ContactEmailMapper.class, ContactPhoneMapper.class})
public interface ContactMapper {

    ContactDto toContactDTO(Contact contact);

    Contact toEntity(ContactDto contactDTO, @MappingTarget Contact contact);
}
