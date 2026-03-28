package com.ram.foodapp.dto.request;

public record CreateOrderRequest(
        int userId,
        double totalPrice,
        String paymentMode
) {}