package com.ram.foodapp.dto.response;

public record OrderItemResponse(
        Integer id,
        int orderId,
        double unitPrice,
        int quantity,
        String itemName,
        String itemDescription,
        String restaurantName,
        double totalPrice
) {}