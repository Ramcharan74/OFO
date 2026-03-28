package com.ram.foodapp.dto.request;

public record UpdateRestaurantRatingRequest(
        int restaurantId,
        double rating,
        int ratingCount
) {}