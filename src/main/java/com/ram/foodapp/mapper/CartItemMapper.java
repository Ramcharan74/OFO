package com.ram.foodapp.mapper;

import com.ram.foodapp.dto.request.AddToCartRequest;
import com.ram.foodapp.dto.response.CartItemResponse;
import com.ram.foodapp.model.cartitem.CartItem;

import java.math.BigDecimal;

public class CartItemMapper {

    public static CartItem toEntity(AddToCartRequest req) {
        return new CartItem(
                req.userId(),
                req.menuItemId(),
                req.quantity(),
                req.itemName(),
                BigDecimal.valueOf(req.unitPrice()),
                req.restaurantName()
        );
    }

    public static CartItemResponse toResponse(CartItem item) {
        double unitPrice = item.getUnitPrice().doubleValue();
        int quantity = item.getQuantity();

        return new CartItemResponse(
                item.getUserId(),
                item.getMenuItemId(),
                quantity,
                item.getItemName(),
                unitPrice,
                item.getRestaurantName(),
                unitPrice * quantity
        );
    }
}