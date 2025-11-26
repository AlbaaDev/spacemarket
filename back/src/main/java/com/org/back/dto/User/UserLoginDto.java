package com.org.back.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UserLoginDto(
        @Email(message = "Email must be a valid email adress.") String email,
        @NotBlank(message = "Password cannot be blank.")  @Size(min = 8, max = 128) String password
) {
}