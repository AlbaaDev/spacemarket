package com.org.back.dto.User;

import com.org.back.interfaces.PasswordMatches;

import jakarta.validation.constraints.NotBlank;

@PasswordMatches
public record UserUpdatePasswordDto(
    @NotBlank(message = "Password is required") String password, 
    @NotBlank(message = "Confirm password is required") String confirmPassword) {
}
