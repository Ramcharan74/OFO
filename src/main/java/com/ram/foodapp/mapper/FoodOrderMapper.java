package com.ram.foodapp.mapper;

import com.ram.foodapp.dto.request.CreateOrderRequest;
import com.ram.foodapp.dto.response.FoodOrderResponse;
import com.ram.foodapp.enums.OrderStatus;
import com.ram.foodapp.model.foodorder.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FoodOrderMapper {

    public static FoodOrder toEntity(CreateOrderRequest req) {
        return new FoodOrder.Builder()
                .userId(req.userId())
                .orderStatus(OrderStatus.PENDING)
                .totalPrice(BigDecimal.valueOf(req.totalPrice()))
                .orderDateTime(LocalDateTime.now())
                .payment(new Payment(
                        false,
                        req.paymentMode()
                ))
                .build();
    }

    public static FoodOrderResponse toResponse(FoodOrder order) {
        return new FoodOrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus() != null ? order.getStatus().name() : null,
                order.getTotalPrice() != null ? order.getTotalPrice().doubleValue() : 0.0,
                order.getOrderDateTime() != null ? order.getOrderDateTime().toString() : null,
                order.getPayment() != null && order.getPayment().isPaid(),
                order.getPayment() != null && order.getPayment().getPaymentMode() != null
                        ? order.getPayment().getPaymentMode().name()
                        : null
        );
    }
}