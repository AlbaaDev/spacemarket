package com.org.back.services;

import java.util.List;
import java.util.Optional;

import com.org.back.dto.User.UserCreateDto;
import com.org.back.dto.User.UserUpdateDto;
import com.org.back.exceptions.UserAlreadyExistsException;
import com.org.back.interfaces.UserService;
import com.org.back.mapper.UserMapper;
import com.org.back.models.User;
import com.org.back.repositories.UserRepository;

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


    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User updateUser(UserUpdateDto userToUpdate) {
       Optional<User> userOptional  = userRepository.findByEmail(userToUpdate.email());
       if(userOptional.isPresent()) {
            userMapper.updateEntityFromDto(userToUpdate, userOptional.get());
            return userRepository.save(userOptional.get());
       } else {
            return null;
       }
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.orElse(null);
    }

    @Override
    public User addUser(UserCreateDto userCreateDto) {
        if (userRepository.findByEmail(userCreateDto.email()).isPresent()) {
            throw new UserAlreadyExistsException("Email already in use. Please use a different email adress.");
        }
        User userToSave = new User();
        userMapper.createEntityFromDto(userCreateDto, userToSave);
        userToSave.setPassword(passwordEncoder.encode(userCreateDto.password()));
        return userRepository.save(userToSave);
    }

    @Override
    public void deleteUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        optionalUser.ifPresent(userRepository::delete);
    }

}
