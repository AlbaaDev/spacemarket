package com.org.back.dto.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateProfileDto(
    @Size(min = 2, max = 45) @NotBlank String firstName, 
    @Size(min = 2, max = 45) @NotBlank String lastName) {
    
}
