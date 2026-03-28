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
                .orderStatus(OrderStatus.CREATED)
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
                order.getStatus().name(),
                order.getTotalPrice().doubleValue(),
                order.getOrderDateTime().toString(),
                order.getPayment().isPaid(),
                order.getPayment().getPaymentMode().name()
        );
    }
}