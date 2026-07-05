package com.ram.foodapp.model.orderitem;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import java.math.BigDecimal;
import java.util.Objects;

public final class OrderItem {

    private final Integer id;
    private final int orderId;
    private final BigDecimal unitPrice;
    private final int quantity;
    private final String itemName;
    private final String itemDescription;
    private final String restaurantName;

    public OrderItem(int orderId, BigDecimal unitPrice, int quantity,String itemName,String itemDescription,String restaurantName) {
        this(null, orderId, unitPrice, quantity,itemName,itemDescription,restaurantName);
    }

    public OrderItem(Integer id, int orderId, BigDecimal unitPrice, int quantity,String itemName,String itemDescription,String restaurantName) {
        validate(orderId, unitPrice, quantity,itemName,itemDescription,restaurantName);
        this.id = id;
        this.orderId = orderId;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.restaurantName = restaurantName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }
    private static void validate(int orderId,
                                 BigDecimal unitPrice,
                                 int quantity,
                                 String itemName,
                                 String itemDescription,
                                 String restaurantName) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("OrderId must be greater than 0");
        }
        if (unitPrice == null || unitPrice.signum() <= 0) {
            throw new IllegalArgumentException("Unit price must be greater than zero");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be null or empty");
        }
        if (itemName.length() > 100) {
            throw new IllegalArgumentException("Item name should not exceed 100 characters");
        }
        if (itemDescription != null && itemDescription.length() > 500) {
            throw new IllegalArgumentException("Item description should not exceed 500 characters");
        }
        if (restaurantName == null || restaurantName.trim().isEmpty()) {
            throw new IllegalArgumentException("Restaurant name cannot be null or empty");
        }
        if (restaurantName.length() > 200) {
            throw new IllegalArgumentException("Restaurant name should not exceed 200 characters");
        }
    }

    public Integer getId() {
        return id;
    }

    public int getOrderId() {
        return orderId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public boolean isPersisted() {
        return id != null;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem)) return false;
        OrderItem that = (OrderItem) o;
        if (this.id != null && that.id != null) {
            return Objects.equals(this.id, that.id);
        }
        return orderId == that.orderId &&
                Objects.equals(unitPrice, that.unitPrice) &&
                quantity == that.quantity;
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(orderId, unitPrice, quantity);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
                '}';
    }
}
