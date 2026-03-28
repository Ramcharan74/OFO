package com.ram.foodapp.mapper;

import com.ram.foodapp.dto.request.CreateMenuItemRequest;
import com.ram.foodapp.dto.response.MenuItemResponse;
import com.ram.foodapp.enums.FoodType;
import com.ram.foodapp.model.menuitem.*;

import java.math.BigDecimal;

public class MenuItemMapper {

    public static MenuItem toEntity(CreateMenuItemRequest req) {

        return new MenuItem.Builder()
                .restaurantId(req.restaurantId())
                .menuDetails(new MenuDetails(
                        req.name(),
                        req.description(),
                        BigDecimal.valueOf(req.price())
                ))
                .category(new Category(
                        FoodType.valueOf(req.category().toUpperCase()),
                        req.subCategory()
                ))
                .imageUrl(req.imageUrl())
                .inventory(new Inventory(req.quantity()))
                .build();
    }

    public static MenuItemResponse toResponse(MenuItem item) {
        return new MenuItemResponse(
                item.getId(),
                item.getRestaurantId(),
                item.getMenuDetails().getName(),
                item.getMenuDetails().getDescription(),
                item.getMenuDetails().getPrice().doubleValue(),
                item.getCategory().getFoodType().name(),
                item.getCategory().getSubCategory(),
                item.getImageUrl(),
                item.getInventory().getQuantity(),
                item.getInventory().isAvailable()
        );
    }
}