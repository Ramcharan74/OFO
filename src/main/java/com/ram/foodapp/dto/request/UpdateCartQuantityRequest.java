package com.ram.foodapp.dto.request;

public record UpdateCartQuantityRequest(
        int userId,
        int menuItemId,
        int quantity
) {}