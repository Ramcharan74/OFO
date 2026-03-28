package com.ram.foodapp.dto.request;

public record CreateOrderItemRequest(
        int orderId,
        double unitPrice,
        int quantity,
        String itemName,
        String itemDescription,
        String restaurantName
) {}