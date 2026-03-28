package com.ram.foodapp.repository;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.orderitem.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemReadRepository {

    Optional<OrderItem> findById(int id);

    List<OrderItem> findByOrderId(int orderId);

    List<OrderItem> findAll(PageRequest pageRequest);

    List<OrderItem> findByMenuId(int menuId,PageRequest pageRequest);
}
