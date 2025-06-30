package com.org.back.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.org.back.dto.User.UserUpdateDto;
import com.org.back.interfaces.UserService;
import com.org.back.mapper.UserMapper;
import com.org.back.models.User;
import com.org.back.repositories.UserRepository;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);

        return users;
    }


    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserById'");
    }

    @Override
    public User updateUserById(Long id, UserUpdateDto updateUser) {
       Optional<User> userOptional  = userRepository.findById(id);
       if(userOptional.isPresent()) {
            userMapper.updateEntityFromDto(updateUser, userOptional.get());
            return userRepository.save(userOptional.get());
       } else {
            return null;
       }
    }


    @Override
    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByEmail'");
    }


    @Override
    public User addUser(User newUser) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addUser'");
    }

    @Override
    public void deleteUserById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUserById'");
    }

}
