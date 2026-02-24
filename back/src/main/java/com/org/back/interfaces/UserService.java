package com.org.back.interfaces;

import java.util.List;
import com.org.back.dto.user.ContactDto;
import com.org.back.dto.user.UserCreateDto;
import com.org.back.dto.user.UserResponseDto;
import com.org.back.dto.user.UserUpdateProfileDto;
import com.org.back.dto.user.UserUpdateSettingsDto;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.exceptions.UserAlreadyExistsException;
import com.org.back.models.User;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface UserService {
    List<UserResponseDto> getAllUsers();

    UserResponseDto addUser(@Valid UserCreateDto userCreateDto) throws UserAlreadyExistsException;

    UserResponseDto getUserById(@NotNull Long id) throws EntityNotFoundException;

    UserResponseDto findUserByEmail(@NotBlank @Email String email) throws EntityNotFoundException;

    void updateUserProfile(User authUser, @Valid UserUpdateProfileDto newUserUpdateDto) 
        throws UserAlreadyExistsException, EntityNotFoundException;

    void updateUserSettings(User authUser, @Valid UserUpdateSettingsDto newUserUpdateDto) 
        throws UserAlreadyExistsException, EntityNotFoundException;

    void deleteUserById(@NotNull Long id) throws EntityNotFoundException;

    List<ContactDto> getContactsByUserId(Long userId) throws EntityNotFoundException;
}
