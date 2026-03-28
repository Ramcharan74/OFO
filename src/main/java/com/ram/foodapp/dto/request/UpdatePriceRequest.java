package com.ram.foodapp.dto.request;

public record UpdatePriceRequest(
        int menuItemId,
        double price
) {}