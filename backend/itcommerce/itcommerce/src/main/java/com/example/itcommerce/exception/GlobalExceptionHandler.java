package com.example.itcommerce.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<String> handleUserRegistrationException(UserRegistrationException e) {
        Throwable cause = e.getCause();

        logger.error("UserRegistrationException occurred: {}", e.getMessage());

        if (cause != null) {
            logger.error("Caused by: {}", cause.getMessage(), cause);
        }

        if (cause instanceof DataIntegrityViolationException) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(String.format("Invalid registration data: %s", cause.getMessage()));
        } else if (cause instanceof DataAccessResourceFailureException) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Database error: %s", cause.getMessage()));
        } else if (cause != null) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Error: %s", cause.getMessage()));
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Unexpected error: %s", e.getMessage()));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        logger.error("MethodArgumentNotValidException occurred: {}", e.getMessage());

        Throwable cause = e.getCause();

        if (cause != null) {
            logger.error("Caused by: {}", cause.getMessage(), cause);
        }

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(UserLoginException.class)
    public ResponseEntity<Map<String, String>> handleLoginException(UserLoginException e) {
        logger.error("UserLoginException occurred: {}", e.getMessage());

        Throwable cause = e.getCause();

        if (cause != null) {
            logger.error("Caused by: {}", cause.getMessage(), cause);
        }
        final Map<String, String > body = new HashMap<>();

        if (cause instanceof IllegalArgumentException) {
            body.put("error", "Email or password is incorrect");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(body);
        } else {
            body.put("error", String.format("Unexpected error: %s", e.getMessage()));
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(body);
        }

    }
}
