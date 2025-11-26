package com.org.back.dto.user;

import com.org.back.interfaces.PasswordMatches;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@PasswordMatches
public record UserUpdatePasswordDto(
    @Size(min = 8, max = 128) @NotBlank(message = "Current password is required") String currentPassword, 
    @Size(min = 8, max = 128) @NotBlank(message = "New password is required") String newPassword) {
}