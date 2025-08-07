package com.org.back.dto.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateProfileDto(
    @Size(min = 2, max = 45) @NotBlank(message = "first name is mandatory") String firstName, 
    @Size(min = 2, max = 45) @NotBlank(message = "last name is mandatory") String lastName) {
}
