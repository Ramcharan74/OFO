package com.ram.foodapp.dto.request;

public record UpdatePaymentStatusRequest(
        int orderId,
        boolean isPaid
) {}