package com.org.back.exceptions;

public class PasswordAlreadyInUseException extends Exception {
    public PasswordAlreadyInUseException(String message) {
        super(message);
    }
} 