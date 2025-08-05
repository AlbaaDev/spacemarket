package com.org.back.dto.User;

public record UserResponseDto(
        String firstName,
        String lastName,
        String email,
        String password,
        String phoneNumber
) {

}