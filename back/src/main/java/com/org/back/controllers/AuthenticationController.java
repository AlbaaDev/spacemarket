package com.org.back.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.back.dto.User.UserLoginResponseDto;
import com.org.back.dto.User.UserLoginDto;
import com.org.back.dto.User.UserRegisterDto;
import com.org.back.models.User;
import com.org.back.services.AuthenticationService;
import com.org.back.services.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final JwtService jwtService;
    
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody UserRegisterDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> authenticate(@RequestBody UserLoginDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        UserLoginResponseDto loginResponse = new UserLoginResponseDto().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}
