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
    
    private final PasswordEncoder passwordEncoder;
    
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
        UserRepository userRepository,
        AuthenticationManager authenticationManager,
        PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public User signup(UserRegisterDto inputForm) {
        User user = new User();
        user.setFirstName(inputForm.getFirstName());
        user.setLastName(inputForm.getLastName());
        user.setEmail(inputForm.getEmail());
        user.setPassword(passwordEncoder.encode(inputForm.getPassword()));

        return userRepository.save(user);
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
