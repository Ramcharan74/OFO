package com.ram.foodapp.repository;

import com.ram.foodapp.dto.request.PageRequest;
import com.ram.foodapp.model.foodorder.FoodOrder;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface OrderReadRepository {

    Optional<FoodOrder> findById(int id);

    List<FoodOrder> findByUserId(int userId, PageRequest pageRequest);

    List<FoodOrder> findByStatus(String status, PageRequest pageRequest);

    List<FoodOrder> findByPaidStatus(Boolean isPaid);

    List<FoodOrder> findAll(PageRequest pageRequest);

    List<FoodOrder> findByDate(Date date);

    public List<FoodOrder> findByPaymentMode(String paymentMode, PageRequest pageRequest);

}
