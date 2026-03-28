package com.ram.foodapp.dto.request;

public record AddToCartRequest(
        int userId,
        int menuItemId,
        int quantity,
        String itemName,
        double unitPrice,
        String restaurantName
) {}