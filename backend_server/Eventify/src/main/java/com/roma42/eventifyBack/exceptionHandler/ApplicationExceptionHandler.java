package com.roma42.eventifyBack.exceptionHandler;

import com.roma42.eventifyBack.exception.*;
import com.nimbusds.jose.proc.BadJWSException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.validation.BindException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(BadJWSException.class)
    public ResponseEntity<Map<String, String>> handleException(BadJWSException e) {
        Map<String, String> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.FORBIDDEN.toString());
        body.put("message", e.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleException(ForbiddenException e) {
        Map<String, String> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.FORBIDDEN.toString());
        body.put("message", e.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }
  
    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<Map<String, String>> handleException(NotAuthorizedException e) {
        Map<String, String> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.UNAUTHORIZED.toString());
        body.put("message", e.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UploadException.class)
    public ResponseEntity<Map<String, String>> handleException(UploadException e) {
        Map<String, String> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.FORBIDDEN.toString());
        body.put("message", e.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }
  
    @ExceptionHandler(ParseException.class)
    public ResponseEntity<Map<String, String>> handleException(ParseException e) {
        Map<String, String> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.FORBIDDEN.toString());
        body.put("message", e.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserCredentialNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleException(UserCredentialNotFoundException e) {
        Map<String, String> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.NOT_FOUND.toString());
        body.put("message", e.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleException(UserNotFoundException e) {
        Map<String, String> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.NOT_FOUND.toString());
        body.put("message", e.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<Map<String, String>> handleException(StorageException e) {
        Map<String, String> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.NOT_FOUND.toString());
        body.put("message", e.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleException(EventNotFoundException e) {
        Map<String, String> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.NOT_FOUND.toString());
        body.put("message", e.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleException(CategoryNotFoundException e) {
        Map<String, String> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.NOT_FOUND.toString());
        body.put("message", e.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmptyPageException.class)
    public ResponseEntity<Map<String, String>> handleException(EmptyPageException e) {
        Map<String, String> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.NOT_FOUND.toString());
        body.put("message", e.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnsupportedTypeException.class)
    public ResponseEntity<Map<String, String>> handleException(UnsupportedTypeException e) {
        Map<String, String> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.toString());
        body.put("message", e.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleException(SQLIntegrityConstraintViolationException e) {
        Map<String, String> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.toString());
        body.put("message", e.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleException(BadRequestException e) {
        Map<String, String> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.toString());
        body.put("message", e.getLocalizedMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            BindException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<Map<String, Object>> handleException(BindException e) {

        List<String> errors = new ArrayList<>();
        e.getFieldErrors()
                .forEach(err -> errors.add(err.getField() + ": " + err.getDefaultMessage()));
        e.getGlobalErrors()
                .forEach(err -> errors.add(err.getObjectName() + ": " + err.getDefaultMessage()));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("error", errors);
        body.put("status", HttpStatus.BAD_REQUEST.toString());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
