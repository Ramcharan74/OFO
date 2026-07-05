package com.ram.foodapp.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    public ResourceNotFoundException(String message,Throwable cause){
        super(message, cause);
    }
}
