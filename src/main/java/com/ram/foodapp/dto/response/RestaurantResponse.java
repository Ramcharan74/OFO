package com.ram.foodapp.dto.response;

public record RestaurantResponse(
        Integer id,
        int userId,
        String name,
        int addressId,
        String status,
        double rating,
        int ratingCount
) {}