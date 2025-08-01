package com.org.back.services;

import org.springframework.stereotype.Service;

import com.org.back.repositories.UserRepository;
import com.org.back.dto.User.UserLoginDto;
import com.org.back.exceptions.BadCredentialsException;
import com.org.back.models.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    public User authenticate(UserLoginDto inputForm) throws BadCredentialsException {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    inputForm.email(),
                    inputForm.password()
                )
            );
        } catch(AuthenticationException exception) {
            throw new BadCredentialsException("Login attempt failed : Invalid email or password.");
        }
        return userRepository.findByEmail(inputForm.email()).orElseThrow();
    }
}
