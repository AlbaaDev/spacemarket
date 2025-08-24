package com.org.back.exceptions;

public class ContactAlreadyExistException extends Exception {
    public ContactAlreadyExistException(String message) {
        super(message);
    }
} 