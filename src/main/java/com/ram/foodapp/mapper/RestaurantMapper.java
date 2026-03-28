package com.ram.foodapp.mapper;

import com.ram.foodapp.dto.request.*;
import com.ram.foodapp.dto.response.RestaurantResponse;
import com.ram.foodapp.enums.RestaurantStatus;
import com.ram.foodapp.model.restaurant.*;

public class RestaurantMapper {

    public static Restaurant toEntity(CreateRestaurantRequest req) {
        return new Restaurant.Builder()
                .userId(req.userId())
                .status(parseStatus(req.status()))
                .restaurantDetails(new RestaurantDetails(
                        req.name(),
                        req.addressId(),
                        0.0,
                        0
                ))
                .build();
    }

    public static RestaurantResponse toResponse(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getUserId(),
                restaurant.getRestaurantDetails().getName(),
                restaurant.getRestaurantDetails().getAddressId(),
                restaurant.getStatus().name(),
                restaurant.getRestaurantDetails().getRating(),
                restaurant.getRestaurantDetails().getRatingCount()
        );
    }

    private static RestaurantStatus parseStatus(String status) {
        try {
            return status == null
                    ? RestaurantStatus.CLOSE
                    : RestaurantStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid restaurant status");
        }
    }
}