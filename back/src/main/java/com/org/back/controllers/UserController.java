package com.org.back.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.back.dto.user.ContactDto;
import com.org.back.dto.user.UserResponseDto;
import com.org.back.dto.user.UserUpdatePasswordDto;
import com.org.back.dto.user.UserUpdateProfileDto;
import com.org.back.dto.user.UserUpdateSettingsDto;
import com.org.back.exceptions.EntityNotFoundException;
import com.org.back.exceptions.PasswordAlreadyInUseException;
import com.org.back.exceptions.PasswordDoesntMatchException;
import com.org.back.exceptions.UserAlreadyExistsException;
import com.org.back.models.ApiResponse;
import com.org.back.models.ResponseUtil;
import com.org.back.models.User;
import com.org.back.services.UserServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

        private final UserServiceImpl userService;

        public UserController(UserServiceImpl userService) {
                this.userService = userService;
        }

        // TODO :
        // @PreAuthorize("hasRole('ADMIN')")
        // @Cacheable("users")
        @GetMapping("/")
        public ResponseEntity<ApiResponse<List<UserResponseDto>>> allUsers(HttpServletRequest request) {
                return ResponseEntity.ok(
                                ResponseUtil.success(
                                                HttpStatus.OK.value(),
                                                "All users retrieved successfully. ",
                                                userService.getAllUsers(),
                                                request.getRequestURI()));
        }

        @PatchMapping("/me/profile")
        public ResponseEntity<Object> updateProfile(
                        @Valid @RequestBody UserUpdateProfileDto newUserProfile,
                        @AuthenticationPrincipal User authUser,
                        HttpServletRequest request) throws EntityNotFoundException {

                userService.updateUserProfile(authUser, newUserProfile);
                return ResponseEntity.noContent().build();
        }

        @PatchMapping("/me/settings")
        public ResponseEntity<Void> updateSettings(
                        @Valid @RequestBody UserUpdateSettingsDto newUserSettings,
                        @AuthenticationPrincipal User authUser,
                        HttpServletRequest request) throws UserAlreadyExistsException, EntityNotFoundException {

                userService.updateUserSettings(authUser, newUserSettings);
                return ResponseEntity.noContent().build();
        }

        @PutMapping("/me/password")
        public ResponseEntity<Void> updatePassword(
                        @Valid @RequestBody UserUpdatePasswordDto newUserPassword,
                        @AuthenticationPrincipal User authUser,
                        HttpServletRequest request) throws PasswordAlreadyInUseException, PasswordDoesntMatchException {

                userService.updateUserPassword(authUser, newUserPassword);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/me")
        public ResponseEntity<ApiResponse<UserResponseDto>> isAuth(@AuthenticationPrincipal User user,
                        HttpServletRequest request)
                        throws EntityNotFoundException {

                return ResponseEntity.ok(
                                ResponseUtil.success(
                                                HttpStatus.NO_CONTENT.value(),
                                                "User is authenticated. ",
                                                userService.findUserByEmail(user.getEmail()),
                                                request.getRequestURI()));
        }

        // @Cacheable("contacts")
        @GetMapping("/{userId}/contacts")
        public ResponseEntity<ApiResponse<List<ContactDto>>> getUserContacts(@PathVariable Long userId,
                        HttpServletRequest request)
                        throws EntityNotFoundException {

                return ResponseEntity.ok(
                                ResponseUtil.success(
                                                HttpStatus.OK.value(),
                                                "Conact users successfuly retrived.",
                                                userService.getContactsByUserId(userId),
                                                request.getRequestURI()));
        }
}