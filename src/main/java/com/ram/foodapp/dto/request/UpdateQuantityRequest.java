package com.ram.foodapp.dto.request;

public record UpdateQuantityRequest(
        int menuItemId,
        int quantity
) {}