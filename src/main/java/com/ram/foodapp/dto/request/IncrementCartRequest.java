package com.ram.foodapp.dto.request;

public record IncrementCartRequest(
        int userId,
        int menuItemId,
        int delta
) {}