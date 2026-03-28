package com.ram.foodapp.dto.response;

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
