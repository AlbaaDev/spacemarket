package com.org.back.dto.User;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateProfileDto(@NotBlank String firstName, @NotBlank String lastName) {
    
}
