package com.ram.foodapp.dto.request;

public record UpdatePaymentModeRequest(
        int orderId,
        String paymentMode
) {}