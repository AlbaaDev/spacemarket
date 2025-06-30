package com.org.back.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.back.dto.User.UserUpdateDto;
import com.org.back.models.User;
import com.org.back.services.UserServiceImpl;

import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/users")
public class UserController {
    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> allUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("{id}")
    public ResponseEntity<User> updateUserById(@PathVariable Long id, @RequestBody UserUpdateDto updatedUser) {
        User savedUser = userService.updateUserById(id, updatedUser);
        if(savedUser != null) {
            return ResponseEntity.ok(savedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // @DeleteMapping("{id}")
    // public ResponseEntity<void> delete(@PathVariable String id) {
    //     userService.delete
    // }

}
