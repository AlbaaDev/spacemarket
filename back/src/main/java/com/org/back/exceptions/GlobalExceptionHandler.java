package com.org.back.exceptions;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.org.back.models.ApiResponse;
import com.org.back.models.ResponseUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ApiResponse<Object> handleUserAlreadyExist(UserAlreadyExistsException ex, HttpServletRequest request) {
        return ResponseUtil.error(HttpStatus.CONFLICT.value(), Arrays.asList(ex.getMessage()), LocalDateTime.now().toString());
    }

    @ExceptionHandler(ContactAlreadyExistException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ApiResponse<Object> handleContactAlreadyExist(ContactAlreadyExistException ex, HttpServletRequest request) {
        return ResponseUtil.error(HttpStatus.CONFLICT.value(), Arrays.asList(ex.getMessage()), LocalDateTime.now().toString());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ApiResponse<Object> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        return ResponseUtil.error(HttpStatus.UNAUTHORIZED.value(), Arrays.asList(ex.getMessage()), LocalDateTime.now().toString());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ApiResponse<Object> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        return ResponseUtil.error(HttpStatus.NOT_FOUND.value(), Arrays.asList(ex.getMessage()), LocalDateTime.now().toString());
    }

    @ExceptionHandler(PasswordDoesntMatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleEntityNotFound(PasswordDoesntMatchException ex, HttpServletRequest request) {
        return ResponseUtil.error(HttpStatus.BAD_REQUEST.value(), Arrays.asList(ex.getMessage()), LocalDateTime.now().toString());
    }

    @ExceptionHandler(PasswordAlreadyInUseException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleEntityNotFound(PasswordAlreadyInUseException ex, HttpServletRequest request) {
        return ResponseUtil.error(HttpStatus.BAD_REQUEST.value(), Arrays.asList(ex.getMessage()), LocalDateTime.now().toString());
    }

    @ExceptionHandler(ConnectException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Object> handleConnectionFailed(ConnectException ex, HttpServletRequest request) {
        return ResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), Arrays.asList(ex.getMessage()),
                LocalDateTime.now().toString());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(
                error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });
        return ResponseUtil.error(HttpStatus.BAD_REQUEST.value(), Arrays.asList(ex.getMessage()), LocalDateTime.now().toString());
    }
}