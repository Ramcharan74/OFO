package com.ram.foodapp.enums;

public enum OrderStatus {
    CREATED, CONFIRMED, CANCELLED, DELIVERED;

    public boolean isFinalState() {
        return this == CANCELLED || this == DELIVERED;
    }
}
