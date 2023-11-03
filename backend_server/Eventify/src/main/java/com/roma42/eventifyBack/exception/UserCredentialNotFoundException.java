package com.roma42.eventifyBack.exception;

public class UserCredentialNotFoundException extends RuntimeException {
    public UserCredentialNotFoundException(String message) {
        super(message);
    }
}
