package com.org.back.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.back.dto.User.UserCreateDto;
import com.org.back.dto.User.UserUpdatePasswordDto;
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
    @Transactional
    public void updateUserProfile(
        User user, 
        @Valid UserUpdateProfileDto newUserUpdateDto) throws EntityNotFoundException {
            
            User userToUpdate = userRepository.findByEmail(user.getEmail()).orElseThrow();
            userMapper.updateEntityProfileFromDto(newUserUpdateDto, userToUpdate);
            userRepository.save(userToUpdate);
    }
    
    @Transactional()
    public void updateUserSettings (
        User user,  
        @Valid UserUpdateSettingsDto newUserUpdateDto) throws UserAlreadyExistsException, EntityNotFoundException {

        Optional<User> userOptional = userRepository.findByEmail(newUserUpdateDto.email());
        if(userOptional.isPresent() && !(userOptional.get().getId().equals(user.getId()))) {
            throw new UserAlreadyExistsException("User already exist with this email. Please choose another one.");
        } 
        userMapper.updateEntitySettingsFromDto(newUserUpdateDto, user);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByEmail(@NotBlank @Email String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User addUser(@Valid UserCreateDto userCreateDto) throws UserAlreadyExistsException {
        if (userRepository.findByEmail(userCreateDto.email()).isPresent()) {
            throw new UserAlreadyExistsException("Email is already in use. Please use a different email adress.");
        }
        User userToSave = new User();
        userMapper.createEntityFromDto(userCreateDto, userToSave);
        userToSave.setPassword(passwordEncoder.encode(userCreateDto.password()));
        return userRepository.save(userToSave);
    }

    @Transactional
    public void deleteUserById(@NotNull Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        optionalUser.ifPresent(userRepository::delete);
    }

    public void updateUserPassword(User authUser, UserUpdatePasswordDto newUserPassowrd) {
        throw new UnsupportedOperationException("Unimplemented method 'updateUserPassword'");
    }
}
