package com.ram.foodapp.dto.request;

public record CreateRestaurantRequest(
        int userId,
        String name,
        int addressId,
        String status
) {}