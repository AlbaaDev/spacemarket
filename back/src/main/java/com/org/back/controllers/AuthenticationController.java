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
import com.org.back.models.ApiResponse;
import com.org.back.models.ResponseUtil;
import com.org.back.models.User;
import com.org.back.security.jwt.JwtService;
import com.org.back.services.AuthenticationService;
import com.org.back.services.UserServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
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

        @PostMapping("/register")
        public ResponseEntity<ApiResponse<UserResponseDto>> registerUser(
                        @Valid @RequestBody UserCreateDto userCreateDto,
                        HttpServletRequest request) throws UserAlreadyExistsException {

                return ResponseEntity.ok(
                                ResponseUtil.success(
                                                HttpStatus.NO_CONTENT.value(),
                                                "User successfuly registred. ",
                                                userService.addUser(userCreateDto),
                                                request.getRequestURI()));
        }

        @PostMapping("/login")
        public ResponseEntity<ApiResponse<UserResponseDto>> authenticate(@Valid @RequestBody UserLoginDto loginUserDto,
                        HttpServletResponse response,
                        HttpServletRequest request) throws BadCredentialsException {

                User authenticatedUser = authenticationService.authenticate(loginUserDto);
                UserResponseDto userResponseDto = userMapper.toUserResponseDto(authenticatedUser);
                String jwtToken = jwtService.generateToken(authenticatedUser);
                ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwtToken)
                                .httpOnly(true)
                                .secure(true)
                                .sameSite("None")
                                .maxAge(Duration.ofHours(2))
                                .path("/")
                                .build();

                response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
                return ResponseEntity.ok(
                                ResponseUtil.success(
                                                HttpStatus.NO_CONTENT.value(),
                                                "User successfully authenticated. ",
                                                userResponseDto,
                                                request.getRequestURI()));
        }

        @GetMapping("/csrf")
        public ResponseEntity<ApiResponse<Object>> csrf(HttpServletRequest request) {
                return ResponseEntity.ok(
                                ResponseUtil.success(
                                                HttpStatus.OK.value(),
                                                "User csrf. ",
                                                null,
                                                request.getRequestURI()));

        }
}
