package com.ram.foodapp.service.impl;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.exception.DataAccessException;
import com.ram.foodapp.exception.ServiceException;
import com.ram.foodapp.model.orderitem.OrderItem;
import com.ram.foodapp.repository.implementation.OrderItemRepositoryImpl;
import com.ram.foodapp.service.OrderItemService;

import java.util.List;
import java.util.Optional;


public class OrderItemServiceImpl implements OrderItemService {
    OrderItemRepositoryImpl orderItemRepositoryImpl;

    public OrderItemServiceImpl(OrderItemRepositoryImpl orderItemRepositoryImpl) {
        this.orderItemRepositoryImpl = orderItemRepositoryImpl;
    }

    @Override
    public OrderItem save(OrderItem orderItem) {
        if (orderItem == null) {
            throw new IllegalArgumentException("OrderItem cannot be null");
        }
        try {
            OrderItem savedOrderItem = orderItemRepositoryImpl.save(orderItem);
            if(savedOrderItem.getItemDescription() != null || savedOrderItem.getRestaurantName() != null){
                savedOrderItem =orderItemRepositoryImpl.update(savedOrderItem);
            }
            return savedOrderItem;
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to save order item", e);
        }
    }


    public OrderItem update(OrderItem orderItem){
        if(orderItem == null){
            throw new IllegalArgumentException("OrderItem cannot be null");
        }
        if(orderItem.getId() == null){
            throw new IllegalArgumentException("OrderItem Id cannot be null");
        }
        if(orderItemRepositoryImpl.findById(orderItem.getId()).isEmpty()){
            throw new IllegalArgumentException("OrderItem not found with Id: "+orderItem.getId());
        }
        try {
            return orderItemRepositoryImpl.update(orderItem);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to update order item", e);
        }
    }

    @Override
    public List<OrderItem> saveAll(List<OrderItem> orderItems) {

        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("OrderItems list cannot be null or empty");
        }
        for (OrderItem item : orderItems) {
            if (item == null ) {
                throw new IllegalArgumentException("OrderItem cannot be null");
            }
        }
        int orderId = orderItems.getFirst().getOrderId();
        for (OrderItem item : orderItems) {
            if (item.getOrderId() != orderId) {
                throw new IllegalArgumentException("All order items must belong to same orderId");
            }
        }
        try {
            return orderItemRepositoryImpl.saveAll(orderItems);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to save order items batch", e);
        }
    }

    @Override
    public void deleteByOrderId(int orderId) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("orderId must be a positive integer");
        }
        try {
            orderItemRepositoryImpl.deleteByOrderId(orderId);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to delete order items for orderId: " + orderId, e);
        }
    }

    @Override
    public Optional<OrderItem> findById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("id must be a positive integer");
        }
        try {
            return orderItemRepositoryImpl.findById(id);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch order item with id: " + id, e);
        }
    }

    @Override
    public List<OrderItem> findByOrderId(int orderId) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("orderId must be a positive integer");
        }
        try {
            return orderItemRepositoryImpl.findByOrderId(orderId);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch order items for orderId: " + orderId, e);
        }
    }

    @Override
    public List<OrderItem> findAll(PageRequest pageRequest) {
        if (pageRequest == null) {
            throw new IllegalArgumentException("PageRequest cannot be null");
        }
        try {
            return orderItemRepositoryImpl.findAll(pageRequest);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch order items", e);
        }
    }

    @Override
    public List<OrderItem> findByMenuId(int menuId, PageRequest pageRequest) {
        if (menuId <= 0) {
            throw new IllegalArgumentException("menuId must be a positive integer");
        }
        if (pageRequest == null) {
            throw new IllegalArgumentException("PageRequest cannot be null");
        }
        try {
            return orderItemRepositoryImpl.findByMenuId(menuId, pageRequest);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch order items for menuId: " + menuId, e);
        }
    }
}
