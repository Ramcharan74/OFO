package com.ram.foodapp.repository;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.restaurant.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantReadRepository {

    Optional<Restaurant> findById(int id);

    List<Restaurant> findByUserId(int userId);

    List<Restaurant> findByAddressId(int addressId);

    List<Restaurant> findByStatus(String status, PageRequest pageRequest);

    List<Restaurant> findAll(PageRequest pageRequest);

}
