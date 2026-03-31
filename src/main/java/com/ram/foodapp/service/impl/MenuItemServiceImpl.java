package com.ram.foodapp.service.impl;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.enums.FoodType;
import com.ram.foodapp.exception.DataAccessException;
import com.ram.foodapp.exception.ServiceException;
import com.ram.foodapp.model.menuitem.MenuItem;
import com.ram.foodapp.repository.MenuItemRepository;
import com.ram.foodapp.service.MenuItemService;

import java.util.List;
import java.util.Optional;


public class MenuItemServiceImpl implements MenuItemService {
    MenuItemRepository menuItemRepository;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public MenuItem save(MenuItem menuItem) {
        if (menuItem == null) {
            throw new IllegalArgumentException("MenuItem cannot be null");
        }
        try {
            return menuItemRepository.save(menuItem);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to save menu item", e);
        }
    }

    @Override
    public Optional<MenuItem> findById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("id must be positive");
        }
        try {
            return menuItemRepository.findById(id);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch menu item with id: " + id, e);
        }
    }

    @Override
    public List<MenuItem> findAll(PageRequest pageRequest) {
        try {
            return menuItemRepository.findAll(pageRequest);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch menu items", e);
        }
    }

    @Override
    public boolean existsById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("id must be positive");
        }
        try {
            return menuItemRepository.existsById(id);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to check menu item with id: " + id, e);
        }
    }

    @Override
    public List<MenuItem> findByAvailability(Boolean isAvailable) {
        if (isAvailable == null) {
            throw new IllegalArgumentException("isAvailable cannot be null");
        }
        try {
            return menuItemRepository.findByAvailability(isAvailable);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch menu items by availability", e);
        }
    }

    @Override
    public List<MenuItem> findByCategory(FoodType foodType) {
        if (foodType == null) {
            throw new IllegalArgumentException("FoodType cannot be null");
        }
        try {
            return menuItemRepository.findByCategory(foodType);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch menu items by category", e);
        }
    }

    @Override
    public List<MenuItem> findByRestaurantId(int restaurantId, PageRequest pageRequest) {
        if (restaurantId <= 0) {
            throw new IllegalArgumentException("restaurantId must be positive");
        }
        try {
            return menuItemRepository.findByRestaurantId(restaurantId, pageRequest);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch menu items for restaurantId: " + restaurantId, e);
        }
    }

    @Override
    public List<MenuItem> findByRestaurantId(int restaurantId) {
        if (restaurantId <= 0) {
            throw new IllegalArgumentException("restaurantId must be positive");
        }
        try {
            return menuItemRepository.findByRestaurantId(restaurantId);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch menu items for restaurantId: " + restaurantId, e);
        }
    }


    @Override
    public List<MenuItem> findAvailableByRestaurantId(int restaurantId, PageRequest pageRequest) {
        if (restaurantId <= 0) {
            throw new IllegalArgumentException("restaurantId must be positive");
        }
        try {
            return menuItemRepository.findAvailableByRestaurantId(restaurantId, pageRequest);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch available menu items", e);
        }
    }

    @Override
    public List<MenuItem> findByCategorynRestaurant(int restaurantId, FoodType category, PageRequest pageRequest) {
        if (restaurantId <= 0) {
            throw new IllegalArgumentException("restaurantId must be positive");
        }
        if (category == null) {
            throw new IllegalArgumentException("category cannot be null");
        }
        try {
            return menuItemRepository.findByCategorynRestaurant(restaurantId, category, pageRequest);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch menu items by category & restaurant", e);
        }
    }

    @Override
    public List<MenuItem> findBySubCategorynRestaurant(int restaurantId, String subCategory, PageRequest pageRequest) {
        if (restaurantId <= 0) {
            throw new IllegalArgumentException("restaurantId must be positive");
        }
        if (subCategory == null || subCategory.isBlank()) {
            throw new IllegalArgumentException("subCategory cannot be null or blank");
        }
        try {
            return menuItemRepository.findBySubCategorynRestaurant(restaurantId, subCategory.trim(), pageRequest);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch menu items by subcategory", e);
        }
    }

    @Override
    public List<MenuItem> searchByName(int restaurantId, String name, PageRequest pageRequest) {
        if (restaurantId <= 0) {
            throw new IllegalArgumentException("restaurantId must be positive");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Search name cannot be null or blank");
        }
        try {
            return menuItemRepository.searchByName(restaurantId, name.trim(), pageRequest);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to search menu items", e);
        }
    }

    @Override
    public List<MenuItem> saveAll(List<MenuItem> menuItems) {
        if (menuItems == null || menuItems.isEmpty()) {
            throw new IllegalArgumentException("MenuItems cannot be null or empty");
        }
        try {
            return menuItemRepository.saveAll(menuItems);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to save menu items batch", e);
        }
    }

    @Override
    public void updateAvailability(int menuItemId, boolean isAvailable) {
        if (menuItemId <= 0) {
            throw new IllegalArgumentException("menuItemId must be positive");
        }
        try {
            menuItemRepository.updateAvailability(menuItemId, isAvailable);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to update availability", e);
        }
    }

    @Override
    public void updatePrice(int menuItemId, double price) {
        if (menuItemId <= 0) {
            throw new IllegalArgumentException("menuItemId must be positive");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        try {
            menuItemRepository.updatePrice(menuItemId, price);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to update price", e);
        }
    }

    @Override
    public void updateQuantity(int menuItemId, int quantity) {
        if (menuItemId <= 0) {
            throw new IllegalArgumentException("menuItemId must be positive");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        try {
            menuItemRepository.updateQuantity(menuItemId, quantity);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to update quantity", e);
        }
    }

    @Override
    public void deleteById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("id must be positive");
        }
        try {
            menuItemRepository.deleteById(id);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to delete menu item", e);
        }
    }
}
