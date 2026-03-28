package com.ram.foodapp.repository;

import com.ram.foodapp.model.orderitem.OrderItem;

import java.util.List;

public interface OrderItemWriteRepository {

    OrderItem save(OrderItem orderItem);

    List<OrderItem> saveAll(List<OrderItem> orderItems);

    void deleteByOrderId(int orderId);
}
