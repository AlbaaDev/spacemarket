package com.org.back.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.back.dto.user.ContactDto;
import com.org.back.dto.user.UserCreateDto;
import com.org.back.dto.user.UserResponseDto;
import com.org.back.dto.user.UserUpdatePasswordDto;
import com.org.back.dto.user.UserUpdateProfileDto;
import com.org.back.dto.user.UserUpdateSettingsDto;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.exceptions.PasswordAlreadyInUseException;
import com.org.back.exceptions.PasswordDoesntMatchException;
import com.org.back.exceptions.UserAlreadyExistsException;
import com.org.back.interfaces.UserService;
import com.org.back.mapper.ContactMapper;
import com.org.back.mapper.UserMapper;
import com.org.back.models.User;
import com.org.back.repositories.ContactRepository;
import com.org.back.repositories.UserRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final UserMapper userMapper;
    private final ContactMapper contactMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
            ContactMapper contactMapper,
            ContactRepository contactRepository, PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
        this.userMapper = userMapper;
        this.contactMapper = contactMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponseDto).toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(@NotNull Long userId) throws EntityNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return userMapper.toUserResponseDto(user);
    }

    @Transactional
    public void updateUserProfile(User user, @Valid UserUpdateProfileDto newUserUpdateDto)
            throws EntityNotFoundException {

        User userToUpdate = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + user.getEmail()));
        userMapper.updateEntityProfileFromDto(newUserUpdateDto, userToUpdate);
        userRepository.save(userToUpdate);
    }

    @Transactional()
    public void updateUserSettings(User user, @Valid UserUpdateSettingsDto newUserUpdateDto)
            throws UserAlreadyExistsException, EntityNotFoundException {

        Optional<User> userOptional = userRepository.findByEmail(newUserUpdateDto.email());
        if (userOptional.isPresent() && !(userOptional.get().getId().equals(user.getId()))) {
            throw new UserAlreadyExistsException("User already exist with this email. Please choose another one.");
        }
        userMapper.updateEntitySettingsFromDto(newUserUpdateDto, user);
        userRepository.save(user);
    }

    @Transactional()
    public void updateUserPassword(User authUser, @Valid UserUpdatePasswordDto newUserPassowrd)
            throws PasswordAlreadyInUseException, PasswordDoesntMatchException {

        boolean passwordMatches = passwordEncoder.matches(newUserPassowrd.currentPassword(), authUser.getPassword());
        boolean passwordAlreadyInUse = passwordEncoder.matches(newUserPassowrd.newPassword(), authUser.getPassword());
        if (!passwordMatches) {
            throw new PasswordDoesntMatchException("The current password doesn't match. Please try again.");
        } else if (passwordAlreadyInUse) {
            throw new PasswordAlreadyInUseException("Password is already in use please choose another one.");
        } else {
            authUser.setPassword(passwordEncoder.encode(newUserPassowrd.newPassword()));
            userRepository.save(authUser);
        }
    }

    @Transactional(readOnly = true)
    public UserResponseDto findUserByEmail(@NotBlank @Email String email) throws EntityNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        return userMapper.toUserResponseDto(user);
    }

    @Transactional
    public UserResponseDto addUser(@Valid UserCreateDto userCreateDto) throws UserAlreadyExistsException {
        if (userRepository.findByEmail(userCreateDto.email()).isPresent()) {
            throw new UserAlreadyExistsException("Email is already in use. Please use a different email address.");
        }
        User userToSave = new User();
        userMapper.createEntityFromDto(userCreateDto, userToSave);
        userToSave.setPassword(passwordEncoder.encode(userCreateDto.password()));
        return userMapper.toUserResponseDto(userRepository.save(userToSave));
    }

    @Transactional
    public void deleteUserById(@NotNull Long id) throws EntityNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    @Override
    public List<ContactDto> getContactsByUserId(Long userId) throws EntityNotFoundException {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return contactRepository.findAllByUser_Id(userId).stream().map(contactMapper::toContactDTO).toList();
    }
}
