package com.ram.foodapp.service.impl;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.enums.RestaurantStatus;
import com.ram.foodapp.model.menuitem.MenuItem;
import com.ram.foodapp.model.restaurant.Restaurant;
import com.ram.foodapp.repository.RestaurantRepository;
import com.ram.foodapp.service.MenuItemService;
import com.ram.foodapp.service.RestaurantService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemService menuItemService;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, MenuItemService menuItemService) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemService = menuItemService;
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        validate(restaurant);
        List<Restaurant> existing = restaurantRepository.findByUserId(restaurant.getUserId());
        boolean duplicate = existing.stream()
                .anyMatch(r -> r.getRestaurantDetails().getName()
                        .equalsIgnoreCase(restaurant.getRestaurantDetails().getName()));
        if (duplicate) {
            throw new IllegalArgumentException("Restaurant with same name already exists for this user");
        }
        return restaurantRepository.save(restaurant);
    }

    @Override
    public List<Restaurant> findAll(PageRequest pageRequest) {
        Objects.requireNonNull(pageRequest, "PageRequest cannot be null");
        return restaurantRepository.findAll(pageRequest);
    }

    public List<Restaurant> findByStatus(String status, PageRequest pageRequest) {
        RestaurantStatus restaurantStatus = parseStatus(status);
        return restaurantRepository.findByStatus(restaurantStatus.name(), pageRequest);
    }

    @Override
    public List<Restaurant> findByUserId(int userId) {
        validateUserId(userId);
        return restaurantRepository.findByUserId(userId);
    }

    @Override
    public List<Restaurant> findByAddressId(int addressId) {
        if (addressId <= 0) {
            throw new IllegalArgumentException("Invalid addressId");
        }
        return restaurantRepository.findByAddressId(addressId);
    }

    @Override
    public Optional<Restaurant> findById(int id) {
        validateRestaurantId(id);
        return restaurantRepository.findById(id);
    }

    @Override
    public void updateStatus(int restaurantId, String status) {
        validateRestaurantId(restaurantId);
        RestaurantStatus newStatus = parseStatus(status);
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        if (restaurant.getStatus() == newStatus) {
            return;
        }
        restaurantRepository.updateStatus(restaurantId, newStatus.name());
    }

    @Override
    public void updateRating(int restaurantId, double newRating, int newRatingCount) {
        validateRestaurantId(restaurantId);
        if (newRating < 0.0 || newRating > 5.0) {
            throw new IllegalArgumentException("Invalid rating");
        }
        if (newRatingCount <= 0) {
            throw new IllegalArgumentException("Rating count must be positive");
        }
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        double currentRating = restaurant.getRestaurantDetails().getRating();
        int currentCount = restaurant.getRestaurantDetails().getRatingCount();
        double updatedRating =
                ((currentRating * currentCount) + (newRating * newRatingCount))
                        / (currentCount + newRatingCount);
        int totalCount = currentCount + newRatingCount;
        restaurantRepository.updateRating(restaurantId, updatedRating, totalCount);
    }

    @Override
    public void deleteById(int id) {
        validateRestaurantId(id);
        if (!restaurantRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Restaurant not found");
        }
        List<MenuItem> menuItemList = menuItemService.findByRestaurantId(id);
        for (MenuItem menuItem : menuItemList) {
            menuItemService.deleteById(menuItem.getId());
        }
        restaurantRepository.deleteById(id);
    }

    private void validate(Restaurant restaurant) {
        Objects.requireNonNull(restaurant, "Restaurant cannot be null");
    }

    private void validateUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid userId");
        }
    }

    private void validateRestaurantId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid restaurantId");
        }
    }

    private RestaurantStatus parseStatus(String status) {
        try {
            return RestaurantStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid restaurant status");
        }
    }
}
