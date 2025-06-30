package com.org.back.interfaces;

import java.util.List;

import com.org.back.dto.User.UserUpdateDto;
import com.org.back.models.User;

public interface UserService {

    List<User> getAllUsers();
    User addUser(User newUser);
    User getUserById(Long id);
    User findUserByEmail(String email);
    User updateUserById(Long id, UserUpdateDto updateUser);
    void deleteUserById(Long id);
}
