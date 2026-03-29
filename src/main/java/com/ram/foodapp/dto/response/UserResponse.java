package com.ram.foodapp.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

public record UserResponse(
        int id,
        String name,
        String email,
        String phoneNo,
        String role,
        boolean active,
        LocalDateTime createdAt
) {}
