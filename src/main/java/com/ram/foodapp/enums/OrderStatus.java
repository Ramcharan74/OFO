package com.ram.foodapp.enums;

public enum OrderStatus {
    PENDING, CONFIRMED, CANCELLED, DELIVERED;

    public boolean isFinalState() {
        return this == CANCELLED || this == DELIVERED;
    }
}
