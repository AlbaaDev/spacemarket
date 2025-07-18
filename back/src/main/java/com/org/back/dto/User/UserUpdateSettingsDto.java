package com.org.back.dto.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateSettingsDto(@Email String email, @NotBlank String password) {

}