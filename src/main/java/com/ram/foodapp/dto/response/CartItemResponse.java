package com.ram.foodapp.dto.response;

public record CartItemResponse(
        int userId,
        int menuItemId,
        int quantity,
        String itemName,
        double unitPrice,
        String restaurantName,
        double totalPrice
) {}