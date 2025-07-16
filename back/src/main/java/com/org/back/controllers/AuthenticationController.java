package com.org.back.controllers;


import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.back.dto.User.UserCreateDto;
import com.org.back.dto.User.UserLoginDto;
import com.org.back.dto.User.UserRegisterDto;
import com.org.back.dto.User.UserResponseDto;
import com.org.back.exceptions.BadCredentialsException;
import com.org.back.exceptions.UserAlreadyExistsException;
import com.org.back.models.User;
import com.org.back.services.AuthenticationService;
import com.org.back.services.JwtService;
import com.org.back.services.UserServiceImpl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.org.back.mapper.UserMapper;

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
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserCreateDto userCreateDto) {
        User savedUser = userService.addUser(userCreateDto);
        UserResponseDto createdUser = userMapper.toUserResponseDto(savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> authenticate(@RequestBody UserLoginDto loginUserDto, HttpServletResponse response) {
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

    @PostMapping("/logout")
    public void logout(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response ) {
        boolean isSecure = false;
        String contextPath = null;
        if (request != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            isSecure = request.isSecure();
            contextPath = request.getContextPath();
        }
        SecurityContext context = SecurityContextHolder.getContext();
        SecurityContextHolder.clearContext();
        context.setAuthentication(null);
        if (response != null) {
            Cookie jwtCookie = new Cookie("jwt", null);
            String cookiePath = StringUtils.hasText(contextPath) ? contextPath : "/";
            jwtCookie.setPath(cookiePath);
            jwtCookie.setMaxAge(0);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(isSecure);
            response.addCookie(jwtCookie);
        }
    }
}
