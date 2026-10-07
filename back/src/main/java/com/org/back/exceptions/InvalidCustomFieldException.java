package com.org.back.exceptions;

public class InvalidCustomFieldException extends RuntimeException {
    public InvalidCustomFieldException(String message) {
        super(message);
    }
}
