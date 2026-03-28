package com.ram.foodapp.service;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.enums.OrderStatus;
import com.ram.foodapp.model.foodorder.FoodOrder;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface FoodOrderService {
    List<FoodOrder> findAll(PageRequest pageRequest);
    List<FoodOrder> findByUserId(int userId, PageRequest pageRequest);
    Optional<FoodOrder> findById(int id);
    List<FoodOrder> findByDate(Date date);
    List<FoodOrder> findByStatus(String status, PageRequest pageRequest);
    List<FoodOrder> findByPaymentMode(String paymentMode, PageRequest pageRequest);
    List<FoodOrder> findByPaidStatus(Boolean isPaid);
    FoodOrder save(FoodOrder foodOrder);
    void updateStatus(int orderId, OrderStatus status);
    void updatePaymentStatus(int orderId, boolean isPaid);
    void updatePaymentMode(int orderId, String paymentMode);
    void updateTotalPrice(int orderId, double totalPrice);
}