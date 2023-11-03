package com.roma42.eventifyBack.exception;

public class NotAuthorizedException extends RuntimeException{
    public NotAuthorizedException(String message) {
        super(message);
    }
}
