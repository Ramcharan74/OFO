package com.ram.foodapp.repository;

import com.ram.foodapp.enums.OrderStatus;
import com.ram.foodapp.model.foodorder.FoodOrder;

public interface OrderWriteRepository {

    FoodOrder save(FoodOrder order);

    void updateStatus(int orderId, OrderStatus status);

    void updatePaymentStatus(int orderId, boolean isPaid);

    void updatePaymentMode(int orderId, String paymentMode);

    void updateTotalPrice(int orderId, double totalPrice);
}
