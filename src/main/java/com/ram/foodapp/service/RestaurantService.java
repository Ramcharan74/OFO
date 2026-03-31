package com.ram.foodapp.service;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.restaurant.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantService {
    Restaurant save(Restaurant restaurant);

    List<Restaurant> findAll(PageRequest pageRequest);

    List<Restaurant> findByStatus(String status, PageRequest pageRequest);

    List<Restaurant> findByUserId(int userId);

    List<Restaurant> findByAddressId(int addressId);

    Optional<Restaurant> findById(int id);

    void updateStatus(int restaurantId, String status);

    void updateRating(int restaurantId, double newRating, int newRatingCount);

    void deleteById(int id);
}
