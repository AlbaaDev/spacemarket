package com.org.back.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.back.dto.User.UserLoginDto;
import com.org.back.dto.User.UserResponseDto;
import com.org.back.dto.User.UserUpdatePasswordDto;
import com.org.back.dto.User.UserUpdateProfileDto;
import com.org.back.dto.User.UserUpdateSettingsDto;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.exceptions.PasswordAlreadyInUseException;
import com.org.back.exceptions.PasswordDoesntMatchException;
import com.org.back.exceptions.UserAlreadyExistsException;
import com.org.back.mapper.UserMapper;
import com.org.back.models.User;
import com.org.back.services.UserServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserServiceImpl userService;
    private final UserMapper userMapper;

    public UserController(UserServiceImpl userService, 
                          UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/")
    public ResponseEntity<List<UserLoginDto>> allUsers() {

        List<UserLoginDto> userList = userService.getAllUsers().stream().map(userMapper::toUserLoginDTO).toList();
        return ResponseEntity.ok(userList);
    }

    @PutMapping("/me/profile")
    public void updateProfile(
        @Valid @RequestBody UserUpdateProfileDto newUserProfile,
        @AuthenticationPrincipal User authUser) throws EntityNotFoundException {

            userService.updateUserProfile(authUser, newUserProfile);
    }

    @PutMapping("/me/settings")
    public void updateSettings(
        @Valid @RequestBody UserUpdateSettingsDto newUserSettings,
        @AuthenticationPrincipal User authUser) throws UserAlreadyExistsException, EntityNotFoundException {

            userService.updateUserSettings(authUser, newUserSettings);
    }

    @PutMapping("/me/password")
    public void updatePassword(
        @Valid @RequestBody UserUpdatePasswordDto newUserPassword,
        @AuthenticationPrincipal User authUser) throws PasswordAlreadyInUseException, PasswordDoesntMatchException {

            userService.updateUserPassword(authUser, newUserPassword);
    }
    
   @GetMapping("/me")
   public ResponseEntity<UserResponseDto> isAuth(@AuthenticationPrincipal User user) {

        Optional<User> userOptional = userService.findUserByEmail(user.getEmail());
        if(userOptional.isPresent()) {
            return ResponseEntity.ok(userMapper.toUserResponseDto(userOptional.get()));
        }
       return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
   }
}
