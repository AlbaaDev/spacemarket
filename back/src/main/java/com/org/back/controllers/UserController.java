package com.org.back.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.back.dto.User.UserUpdateDto;
import com.org.back.models.User;
import com.org.back.repositories.UserRepository;
import com.org.back.services.JwtService;
import com.org.back.services.UserServiceImpl;

import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/users")
public class UserController {
    private final UserServiceImpl userService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public UserController(UserServiceImpl userService, 
                          JwtService jwtService, 
                          UserDetailsService userDetailsService,
                          UserRepository userRepository) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
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

    @GetMapping("/me")
    public ResponseEntity<User> isAuth(@CookieValue(required = false) String jwt) {
        final String userEmail = jwtService.extractUsername(jwt);
        if (jwt != null && jwtService.isTokenValid(jwt, userDetailsService.loadUserByUsername(userEmail))) {
            return ResponseEntity.ok(userRepository.findByEmail(userEmail).get());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
