package com.example.itcommerce.exception;

public class UserLoginException extends RuntimeException {
    public UserLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
