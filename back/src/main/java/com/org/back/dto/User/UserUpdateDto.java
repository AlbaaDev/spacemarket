package com.org.back.dto.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateDto(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @Email String email,
    @NotNull Long version
) {
    
}
