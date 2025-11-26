package com.org.back.dto.user;

public record UserResponseDto(
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {

}