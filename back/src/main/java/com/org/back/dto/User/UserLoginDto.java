package com.org.back.dto.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record UserLoginDto(
        @NotBlank(message = "First name is mandatory.") String firstName,
        @NotBlank(message = "Last name is mandatory.") String lastName,
        @Email(message = "Email must be a valid email adress.") String email,
        @NotBlank(message = "Password cannot be blank.") String password,
        @NotBlank(message = "Phone number is mandatory.") String phoneNumber
) {

}