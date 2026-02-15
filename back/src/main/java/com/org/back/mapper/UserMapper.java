package com.org.back.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.org.back.dto.user.UserCreateDto;
import com.org.back.dto.user.UserLoginDto;
import com.org.back.dto.user.UserResponseDto;
import com.org.back.dto.user.UserUpdateProfileDto;
import com.org.back.dto.user.UserUpdateSettingsDto;
import com.org.back.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserLoginDto toUserLoginDTO(User user);
    UserResponseDto toUserResponseDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntityProfileFromDto(UserUpdateProfileDto userUpdaDto, @MappingTarget User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    void updateEntitySettingsFromDto(UserUpdateSettingsDto userSettingsDto, @MappingTarget User user);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void createEntityFromDto(UserCreateDto userCreateDto, @MappingTarget User user);

}
