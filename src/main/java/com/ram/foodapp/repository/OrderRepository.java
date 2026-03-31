package com.ram.foodapp.repository;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.enums.OrderStatus;
import com.ram.foodapp.model.foodorder.FoodOrder;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    //read
    Optional<FoodOrder> findById(int id);

    List<FoodOrder> findByUserId(int userId, PageRequest pageRequest);

    List<FoodOrder> findByStatus(String status, PageRequest pageRequest);

    List<FoodOrder> findByPaidStatus(Boolean isPaid);

    List<FoodOrder> findAll(PageRequest pageRequest);

    List<FoodOrder> findByDate(Date date);

    public List<FoodOrder> findByPaymentMode(String paymentMode, PageRequest pageRequest);

    //write
    FoodOrder save(FoodOrder order);

    void updateStatus(int orderId, OrderStatus status);

    void updatePaymentStatus(int orderId, boolean isPaid);

    void updatePaymentMode(int orderId, String paymentMode);

    void updateTotalPrice(int orderId, double totalPrice);

}
