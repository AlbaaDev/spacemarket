package com.org.back.dto.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateSettingsDto(
        @Size(min = 2, max = 128) @Email String email,
        @Size(min = 10, max = 12, message = "Phone number should be between 10 and 12") String phoneNumber){
}