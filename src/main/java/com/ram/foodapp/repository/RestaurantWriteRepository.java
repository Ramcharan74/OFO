package com.ram.foodapp.repository;

import com.ram.foodapp.model.restaurant.Restaurant;

public interface RestaurantWriteRepository {

    Restaurant save(Restaurant restaurant);

    void updateStatus(int restaurantId, String status);

    void updateRating(int restaurantId, double rating, int ratingCount);

    void deleteById(int id);
}
