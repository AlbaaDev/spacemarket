package com.org.back.controllers;


import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.back.dto.user.UserCreateDto;
import com.org.back.dto.user.UserLoginDto;
import com.org.back.dto.user.UserResponseDto;
import com.org.back.exceptions.BadCredentialsException;
import com.org.back.exceptions.UserAlreadyExistsException;
import com.org.back.mapper.UserMapper;
import com.org.back.models.User;
import com.org.back.security.jwt.JwtService;
import com.org.back.services.AuthenticationService;
import com.org.back.services.UserServiceImpl;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserServiceImpl userService;
    private final UserMapper userMapper;


    public AuthenticationController(JwtService jwtService, 
                                    AuthenticationService authenticationService, 
                                    UserServiceImpl userServiceImpl,
                                    UserMapper userMapper) {
                                        
        this.userService = userServiceImpl;
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userMapper = userMapper;
    }

    @PostMapping("register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserCreateDto userCreateDto) throws UserAlreadyExistsException {
        User savedUser = userService.addUser(userCreateDto);
        UserResponseDto createdUser = userMapper.toUserResponseDto(savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> authenticate(@Valid @RequestBody UserLoginDto loginUserDto, HttpServletResponse response) throws BadCredentialsException {
            User authenticatedUser = authenticationService.authenticate(loginUserDto);
            UserResponseDto userResponseDto = userMapper.toUserResponseDto(authenticatedUser);
            String jwtToken = jwtService.generateToken(authenticatedUser);
            ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwtToken)
                                                     .httpOnly(true)
                                                     .secure(true)
                                                     .sameSite("Strict")
                                                     .maxAge(Duration.ofHours(2))
                                                     .path("/")
                                                     .build();
    
            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
            return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping("/csrf")
    public ResponseEntity<Void> csrf() {
        return ResponseEntity.ok().build();
    }
    
}
