package com.ram.foodapp.dto.response;

public record MenuItemResponse(
        Integer id,
        Integer restaurantId,
        String name,
        String description,
        double price,
        String category,
        String subCategory,
        String imageUrl,
        int quantity,
        boolean isAvailable
) {}