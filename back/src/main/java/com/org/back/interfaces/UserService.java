package com.org.back.interfaces;

import java.util.List;
import java.util.Optional;

import com.org.back.dto.User.UserCreateDto;
import com.org.back.dto.User.UserUpdateProfileDto;
import com.org.back.dto.User.UserUpdateSettingsDto;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.exceptions.UserAlreadyExistsException;
import com.org.back.models.User;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface UserService {
    List<User> getAllUsers();
    User addUser(@Valid UserCreateDto userCreateDto) throws UserAlreadyExistsException;
    Optional<User> getUserById(@NotNull Long id);
    Optional<User> findUserByEmail(@NotBlank @Email String email);
    void updateUserProfile(@NotNull Long userId, @Valid UserUpdateProfileDto newUserUpdate) throws UserAlreadyExistsException, EntityNotFoundException;
    void updateUserSettings(@NotNull Long userId, @Valid UserUpdateSettingsDto newUserUpdate) throws UserAlreadyExistsException, EntityNotFoundException;
    void deleteUserById(@NotNull Long id);
}
