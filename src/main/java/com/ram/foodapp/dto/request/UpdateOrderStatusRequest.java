package com.ram.foodapp.dto.request;

public record UpdateOrderStatusRequest(
        int orderId,
        String status
) {}