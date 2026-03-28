package com.ram.foodapp.model.cartitem;

import java.math.BigDecimal;
import java.util.Objects;

public class CartItem {
    private final int userId;
    private final int menuItemId;
    private final int quantity;
    private final String itemName;
    private final BigDecimal unitPrice;
    private final String restaurantName;

    public CartItem(int userId, int menuItemId, int quantity, String itemName, BigDecimal unitPrice, String restaurantName) {
        if (userId <= 0) throw new IllegalArgumentException("Invalid userId");
        if (menuItemId <= 0) throw new IllegalArgumentException("Invalid menuItemId");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0");
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be null or empty");
        }
        itemName = itemName.trim();
        if (itemName.length() > 100) {
            throw new IllegalArgumentException("Item name should not exceed 100 characters");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("Unit price cannot be null");
        }
        if (unitPrice.signum() <= 0) {
            throw new IllegalArgumentException("Unit price must be greater than 0");
        }
        if (unitPrice.scale() > 2) {
            throw new IllegalArgumentException("Unit price cannot have more than 2 decimal places");
        }
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.restaurantName = restaurantName;
        this.userId = userId;
        this.menuItemId = menuItemId;
        this.quantity = quantity;
    };

    public int getUserId() {
        return userId;
    }

    public int getMenuItemId() {
        return menuItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return userId == cartItem.userId && menuItemId == cartItem.menuItemId && quantity == cartItem.quantity && Objects.equals(itemName, cartItem.itemName) && Objects.equals(unitPrice, cartItem.unitPrice) && Objects.equals(restaurantName, cartItem.restaurantName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, menuItemId, quantity, itemName, unitPrice, restaurantName);
    }
}
