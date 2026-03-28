package com.ram.foodapp.repository;

import com.ram.foodapp.enums.FoodType;
import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.menuitem.MenuItem;

import java.util.List;
import java.util.Optional;

public interface MenuItemReadRepository {

    Optional<MenuItem> findById(int id);

    List<MenuItem> findAll(PageRequest pageRequest);

    List<MenuItem> findByAvailability(Boolean isAvailable);

    List<MenuItem> findByCategory(FoodType foodType);

    List<MenuItem> findByRestaurantId(int restaurantId);

    List<MenuItem> findByRestaurantId(int restaurantId, PageRequest pageRequest);

    List<MenuItem> findAvailableByRestaurantId(int restaurantId, PageRequest pageRequest);

    List<MenuItem> findByCategorynRestaurant(int restaurantId, FoodType category, PageRequest pageRequest);

    List<MenuItem> findBySubCategorynRestaurant(int restaurantId, String subCategory, PageRequest pageRequest);

    List<MenuItem> searchByName(int restaurantId, String name, PageRequest pageRequest);

}
