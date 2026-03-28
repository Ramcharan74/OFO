package com.ram.foodapp.dto.request;

public record UpdateRestaurantStatusRequest(
        int restaurantId,
        String status
) {}
