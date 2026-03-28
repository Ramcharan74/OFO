package com.ram.foodapp.service.impl;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.enums.OrderStatus;
import com.ram.foodapp.enums.PaymentMode;
import com.ram.foodapp.exception.DataAccessException;
import com.ram.foodapp.exception.ServiceException;
import com.ram.foodapp.model.foodorder.FoodOrder;
import com.ram.foodapp.repository.implementation.OrderRepositoryImpl;
import com.ram.foodapp.service.FoodOrderService;
import java.sql.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FoodOrderServiceImpl implements FoodOrderService {
    OrderRepositoryImpl orderRepositoryImpl;

    public FoodOrderServiceImpl(OrderRepositoryImpl orderRepositoryImpl) {
        this.orderRepositoryImpl = orderRepositoryImpl;
    }

    @Override
    public List<FoodOrder> findAll(PageRequest pageRequest) {
        try {
            Objects.requireNonNull(pageRequest);
            return orderRepositoryImpl.findAll(pageRequest);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch orders", e);
        }
    }

    @Override
    public List<FoodOrder> findByUserId(int userId, PageRequest pageRequest) {
        if (userId <= 0) {
            throw new IllegalArgumentException("userId must be positive");
        }
        Objects.requireNonNull(pageRequest);
        try {
            return orderRepositoryImpl.findByUserId(userId, pageRequest);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch orders for userId: " + userId, e);
        }
    }

    @Override
    public Optional<FoodOrder> findById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("id must be positive");
        }
        try {
            return orderRepositoryImpl.findById(id);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch order with id: " + id, e);
        }
    }

    @Override
    public List<FoodOrder> findByDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        try {
            return orderRepositoryImpl.findByDate(date);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch orders by date", e);
        }
    }

    @Override
    public List<FoodOrder> findByStatus(String status, PageRequest pageRequest) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status cannot be null or blank");
        }
        Objects.requireNonNull(pageRequest);
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.trim().toUpperCase());
            return orderRepositoryImpl.findByStatus(orderStatus.name(), pageRequest);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch orders by status", e);
        }
    }

    @Override
    public List<FoodOrder> findByPaymentMode(String paymentMode, PageRequest pageRequest) {
        if (paymentMode == null || paymentMode.isBlank()) {
            throw new IllegalArgumentException("Payment mode cannot be null or blank");
        }
        Objects.requireNonNull(pageRequest);
        try {
            PaymentMode mode = PaymentMode.valueOf(paymentMode.trim().toUpperCase());
            return orderRepositoryImpl.findByPaymentMode(mode.name(), pageRequest);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment mode: " + paymentMode);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch orders by payment mode", e);
        }
    }

    @Override
    public List<FoodOrder> findByPaidStatus(Boolean isPaid) {
        if (isPaid == null) {
            throw new IllegalArgumentException("isPaid cannot be null");
        }
        try {
            return orderRepositoryImpl.findByPaidStatus(isPaid);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch orders by paid status", e);
        }
    }

    @Override
    public FoodOrder save(FoodOrder foodOrder) {

        if (foodOrder == null) {
            throw new IllegalArgumentException("FoodOrder cannot be null");
        }

        try {
            return orderRepositoryImpl.save(foodOrder);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to save order", e);
        }
    }

    @Override
    public void updateStatus(int orderId, OrderStatus status) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("orderId must be positive");
        }
        if (status == null) {
            throw new IllegalArgumentException("status cannot be null");
        }
        try {
            orderRepositoryImpl.updateStatus(orderId, status);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to update order status", e);
        }
    }

    @Override
    public void updatePaymentStatus(int orderId, boolean isPaid) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("orderId must be positive");
        }
        try {
            orderRepositoryImpl.updatePaymentStatus(orderId, isPaid);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to update payment status", e);
        }
    }

    @Override
    public void updatePaymentMode(int orderId, String paymentMode) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("orderId must be positive");
        }
        if (paymentMode == null || paymentMode.isBlank()) {
            throw new IllegalArgumentException("paymentMode cannot be null");
        }
        try {
            PaymentMode mode = PaymentMode.valueOf(paymentMode.trim().toUpperCase());
            orderRepositoryImpl.updatePaymentMode(orderId, mode.name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment mode: " + paymentMode);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to update payment mode", e);
        }
    }

    @Override
    public void updateTotalPrice(int orderId, double totalPrice) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("orderId must be positive");
        }
        if (totalPrice <= 0) {
            throw new IllegalArgumentException("totalPrice must be greater than zero");
        }
        try {
            orderRepositoryImpl.updateTotalPrice(orderId, totalPrice);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to update total price", e);
        }
    }
}
