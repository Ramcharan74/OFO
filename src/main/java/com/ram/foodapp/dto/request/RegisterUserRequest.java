package com.ram.foodapp.dto.request;

public record RegisterUserRequest(
        String name,
        String email,
        String phoneNo,
        String password,
        String role
) {
    public RegisterUserRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password too short");
        }
    }
}