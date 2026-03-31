package com.ram.foodapp.service;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.orderitem.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemService {
    OrderItem save(OrderItem orderItem);

    List<OrderItem> saveAll(List<OrderItem> orderItems);

    void deleteByOrderId(int orderId);

    Optional<OrderItem> findById(int id);

    List<OrderItem> findByOrderId(int orderId);

    List<OrderItem> findAll(PageRequest pageRequest);

    List<OrderItem> findByMenuId(int menuId, PageRequest pageRequest);
}