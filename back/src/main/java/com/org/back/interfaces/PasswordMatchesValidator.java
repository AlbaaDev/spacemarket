package com.org.back.interfaces;

import com.org.back.dto.User.UserUpdatePasswordDto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserUpdatePasswordDto> {

    @Override
    public boolean isValid(UserUpdatePasswordDto dto, ConstraintValidatorContext context) {
        return dto.password() != null &&
               dto.password().equals(dto.confirmPassword());
    }
}
