package com.org.back.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.common.util.StringUtils;
import com.org.back.dto.User.UserLoginDto;
import com.org.back.dto.User.UserResponseDto;
import com.org.back.dto.User.UserUpdateProfileDto;
import com.org.back.dto.User.UserUpdateSettingsDto;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.exceptions.UserAlreadyExistsException;
import com.org.back.mapper.UserMapper;
import com.org.back.models.User;
import com.org.back.repositories.UserRepository;
import com.org.back.security.jwt.JwtService;
import com.org.back.services.UserServiceImpl;

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

    @PutMapping("/update/profile")
    public void updateProfile(
        @RequestBody UserUpdateProfileDto newUserProfile,
        @AuthenticationPrincipal User authUser) throws EntityNotFoundException {

            userService.updateUserProfile(authUser, newUserProfile);
    }

    @PutMapping("/update/settings")
    public void updateSettings(
        @RequestBody UserUpdateSettingsDto newUserSettings,
        @AuthenticationPrincipal User authUser) throws UserAlreadyExistsException, EntityNotFoundException {

            userService.updateUserSettings(authUser, newUserSettings);
    }
    
   @GetMapping("/me")
   public ResponseEntity<UserResponseDto> isAuth(@CookieValue(required = false) String jwt) {

       final String userEmail = jwtService.extractUsername(jwt);
       if (jwt != null && StringUtils.isNotBlank(userEmail) && jwtService.isTokenValid(jwt, userDetailsService.loadUserByUsername(userEmail))) {
           Optional<User> userOptional = userRepository.findByEmail(userEmail);
           if(userOptional.isPresent()) {
               return ResponseEntity.ok(userMapper.toUserResponseDto(userOptional.get()));
           }
       }
       return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
   }

    
//    @GetMapping("/me")
//    public ResponseEntity<UserResponseDto> authenticatedUser(@AuthenticationPrincipal User authUser) {
//        // Thanks to JwtAuthenticationFilter and @AuthenticationPrincipal, authUser is guaranteed to be non-null and authenticated.
//        UserResponseDto userResponseDto = userMapper.toUserResponseDto(authUser);
//        return ResponseEntity.ok(userResponseDto);
//    }
}
