package com.org.back.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.org.back.dto.User.UserCreateDto;
import com.org.back.dto.User.UserLoginDto;
import com.org.back.dto.User.UserResponseDto;
import com.org.back.dto.User.UserUpdateDto;
import com.org.back.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserLoginDto toUserLoginDTO(User user);
    UserResponseDto toUserResponseDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntityFromDto(UserUpdateDto userUpdaDto, @MappingTarget User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void createEntityFromDto(UserCreateDto userCreateDto, @MappingTarget User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(UserUpdateDto UserUpdateDto);
}
