package com.org.back.mapper;

import com.org.back.dto.user.ContactPhoneDto;
import com.org.back.models.ContactPhone;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContactPhoneMapper {
    ContactPhoneDto toContactPhoneDTO(ContactPhone contactPhone);
}
