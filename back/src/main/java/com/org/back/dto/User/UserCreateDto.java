package com.org.back.dto.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateDto(@NotBlank String firstName, @NotBlank String lastName, @Email String email, @NotBlank String password) {

}
