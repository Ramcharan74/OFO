package com.ram.foodapp.service;

import com.ram.foodapp.enums.FoodType;
import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.menuitem.MenuItem;

import java.util.List;
import java.util.Optional;

public interface MenuItemService {
    MenuItem save(MenuItem menuItem);
    Optional<MenuItem> findById(int id);
    boolean existsById(int id);
    List<MenuItem> findAll(PageRequest pageRequest);
    List<MenuItem> findByAvailability(Boolean isAvailable);
    List<MenuItem> findByCategory(FoodType foodType);
    List<MenuItem> findByRestaurantId(int restaurantId);
    List<MenuItem> findByRestaurantId(int restaurantId, PageRequest pageRequest);
    List<MenuItem> findAvailableByRestaurantId(int restaurantId, PageRequest pageRequest);
    List<MenuItem> findByCategorynRestaurant(int restaurantId, FoodType category, PageRequest pageRequest);
    List<MenuItem> findBySubCategorynRestaurant(int restaurantId, String subCategory, PageRequest pageRequest);
    List<MenuItem> searchByName(int restaurantId, String name, PageRequest pageRequest);
    List<MenuItem> saveAll(List<MenuItem> menuItems);
    void updateAvailability(int menuItemId, boolean isAvailable);
    void updatePrice(int menuItemId, double price);
    void updateQuantity(int menuItemId, int quantity);
    void deleteById(int id);
}