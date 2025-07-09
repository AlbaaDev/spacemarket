package com.org.back.services;

import org.springframework.stereotype.Service;

import com.org.back.repositories.UserRepository;
import com.org.back.dto.User.UserLoginDto;
import com.org.back.dto.User.UserRegisterDto;
import com.org.back.models.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    
    
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
        UserRepository userRepository,
        AuthenticationManager authenticationManager
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    public User authenticate(UserLoginDto inputForm) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    inputForm.getEmail(),
                    inputForm.getPassword()
                )
        );


        return userRepository.findByEmail(inputForm.getEmail())
                .orElseThrow();
    }
}
