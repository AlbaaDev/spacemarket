package com.org.back.exceptions;

public class PasswordDoesntMatchException extends Exception {
    public PasswordDoesntMatchException(String message) {
        super(message);
    }
}
