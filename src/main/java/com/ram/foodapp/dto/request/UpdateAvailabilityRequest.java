package com.ram.foodapp.dto.request;

public record UpdateAvailabilityRequest(
        int menuItemId,
        boolean isAvailable
) {}