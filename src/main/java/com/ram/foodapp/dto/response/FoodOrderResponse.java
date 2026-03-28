package com.ram.foodapp.dto.response;

public record FoodOrderResponse(
        Integer id,
        int userId,
        String status,
        double totalPrice,
        String orderDateTime,
        boolean isPaid,
        String paymentMode
) {}