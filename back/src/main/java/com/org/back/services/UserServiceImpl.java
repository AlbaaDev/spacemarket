package com.org.back.services;

import java.util.List;
import java.util.Optional;

import com.org.back.dto.User.UserCreateDto;
import com.org.back.dto.User.UserUpdateProfileDto;
import com.org.back.dto.User.UserUpdateSettingsDto;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.exceptions.UserAlreadyExistsException;
import com.org.back.interfaces.UserService;
import com.org.back.mapper.UserMapper;
import com.org.back.models.User;
import com.org.back.repositories.UserRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(@NotNull Long userId) {
        return userRepository.findById(userId);
    }

    public void updateUserProfile(@NotNull Long userId, @Valid UserUpdateProfileDto newUserUpdateDto) throws UserAlreadyExistsException, EntityNotFoundException {
        Optional<User> userFound = getUserById(userId);
        if(userFound.isPresent()) {
             userMapper.updateEntityProfileFromDto(newUserUpdateDto, userFound.get());
             userRepository.save(userFound.get());
        } else {
            throw new EntityNotFoundException("User not found with this ID.");
        }
    }

    public void updateUserSettings(@NotNull Long userId, @Valid UserUpdateSettingsDto newUserUpdateDto) throws UserAlreadyExistsException, EntityNotFoundException {
        Optional<User> userOptional = findUserByEmail(newUserUpdateDto.email());
        if(userOptional.isPresent() && !userOptional.get().getId().equals(userId)) {
            throw new UserAlreadyExistsException("User already exist with this email. Please choose another one.");
        } 
        Optional<User> userFound = getUserById(userId);
        if(userFound.isPresent()) {
            userMapper.updateEntitySettingsFromDto(newUserUpdateDto, userFound.get());
            userFound.get().setPassword(passwordEncoder.encode(newUserUpdateDto.password()));
            userRepository.save(userFound.get());
       } else {
           throw new EntityNotFoundException("User not found with this ID.");
       }
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByEmail(@NotBlank @Email String email) {
        return userRepository.findByEmail(email);
    }

    public User addUser(@Valid UserCreateDto userCreateDto) throws UserAlreadyExistsException {
        if (userRepository.findByEmail(userCreateDto.email()).isPresent()) {
            throw new UserAlreadyExistsException("Email is already in use. Please use a different email adress.");
        }
        User userToSave = new User();
        userMapper.createEntityFromDto(userCreateDto, userToSave);
        userToSave.setPassword(passwordEncoder.encode(userCreateDto.password()));
        return userRepository.save(userToSave);
    }

    public void deleteUserById(@NotNull Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        optionalUser.ifPresent(userRepository::delete);
    }
}
