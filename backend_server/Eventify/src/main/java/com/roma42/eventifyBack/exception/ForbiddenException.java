package com.roma42.eventifyBack.exception;

public class ForbiddenException extends RuntimeException{
    public ForbiddenException(String message) {
        super(message);
    }
}
