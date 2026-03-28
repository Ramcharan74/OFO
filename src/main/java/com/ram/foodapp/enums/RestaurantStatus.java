package com.ram.foodapp.enums;

public enum RestaurantStatus {
    OPEN,
    CLOSE;
    public boolean isOpen() {
        return this == OPEN;
    }
}
