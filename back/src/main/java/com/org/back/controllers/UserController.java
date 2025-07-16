package com.org.back.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.back.dto.User.UserLoginDto;
import com.org.back.dto.User.UserResponseDto;
import com.org.back.dto.User.UserUpdateDto;
import com.org.back.mapper.UserMapper;
import com.org.back.models.User;
import com.org.back.repositories.UserRepository;
import com.org.back.services.JwtService;
import com.org.back.services.UserServiceImpl;

import io.micrometer.common.util.StringUtils;

import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserServiceImpl userService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserController(UserServiceImpl userService, 
                          JwtService jwtService, 
                          UserDetailsService userDetailsService,
                          UserRepository userRepository,
                          UserMapper userMapper) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @GetMapping("/")
    public ResponseEntity<List<UserLoginDto>> allUsers() {
        List<UserLoginDto> userList = userService.getAllUsers().stream().map(userMapper::toUserLoginDTO).toList();
        return ResponseEntity.ok(userList);
    }

    @PostMapping("/update")
    public ResponseEntity<UserResponseDto> update(
        @RequestBody UserUpdateDto updatedUser, 
        @CookieValue(required = false) String jwt) {

        String email = jwtService.extractUsername(jwt);
        userRepository.findByEmail(email);
        User savedUser = userService.updateUser(updatedUser);
        if(savedUser != null) {
            return ResponseEntity.ok(userMapper.toUserResponseDto(savedUser));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // @DeleteMapping("{id}")
    // public ResponseEntity<void> delete(@PathVariable String id) {
    //     userService.delete
    // }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> isAuth(@CookieValue(required = false) String jwt) {
        final String userEmail = jwtService.extractUsername(jwt);
        if (jwt != null && StringUtils.isNotBlank(userEmail) && jwtService.isTokenValid(jwt, userDetailsService.loadUserByUsername(userEmail))) {
            return ResponseEntity.ok(userMapper.toUserResponseDto(userRepository.findByEmail(userEmail).get()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
