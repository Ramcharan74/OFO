package com.ram.foodapp.dto.request;

public record CreateMenuItemRequest(
        int restaurantId,
        String name,
        String description,
        double price,
        String category,
        String subCategory,
        String imageUrl,
        int quantity
) {}