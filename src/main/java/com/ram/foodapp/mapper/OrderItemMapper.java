package com.ram.foodapp.mapper;

import com.ram.foodapp.dto.request.CreateOrderItemRequest;
import com.ram.foodapp.dto.response.OrderItemResponse;
import com.ram.foodapp.model.orderitem.OrderItem;

import java.math.BigDecimal;

public class OrderItemMapper {

    public static OrderItem toEntity(CreateOrderItemRequest req) {
        return new OrderItem(
                req.orderId(),
                BigDecimal.valueOf(req.unitPrice()),
                req.quantity(),
                req.itemName(),
                req.itemDescription(),
                req.restaurantName()
        );
    }

    public static OrderItemResponse toResponse(OrderItem item) {
        double unitPrice = item.getUnitPrice().doubleValue();
        int quantity = item.getQuantity();

        return new OrderItemResponse(
                item.getId(),
                item.getOrderId(),
                unitPrice,
                quantity,
                item.getItemName(),
                item.getItemDescription(),
                item.getRestaurantName(),
                unitPrice * quantity
        );
    }
}