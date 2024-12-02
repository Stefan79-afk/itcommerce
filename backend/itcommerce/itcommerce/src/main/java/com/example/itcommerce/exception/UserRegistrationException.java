package com.example.itcommerce.exception;

import org.springframework.http.HttpStatus;

public class UserRegistrationException extends RuntimeException{

    public UserRegistrationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
