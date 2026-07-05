package com.ram.foodapp.controller;

import com.ram.foodapp.dto.request.*;
import com.ram.foodapp.dto.response.ApiResponse;
import com.ram.foodapp.dto.response.FoodOrderResponse;
import com.ram.foodapp.enums.OrderStatus;
import com.ram.foodapp.exception.ResourceNotFoundException;
import com.ram.foodapp.mapper.FoodOrderMapper;
import com.ram.foodapp.model.foodorder.FoodOrder;
import com.ram.foodapp.service.FoodOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class FoodOrderController {

    private static final Logger logger = LoggerFactory.getLogger(FoodOrderController.class);

    private final FoodOrderService foodOrderService;

    public FoodOrderController(FoodOrderService foodOrderService) {
        this.foodOrderService = foodOrderService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FoodOrderResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        List<FoodOrderResponse> list = foodOrderService.findAll(new PageRequest(page, size))
                .stream()
                .map(FoodOrderMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Orders fetched", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodOrderResponse>> getById(@PathVariable int id) {
        FoodOrder order = foodOrderService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return ResponseEntity.ok(ApiResponse.success("Order fetched", FoodOrderMapper.toResponse(order)));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<FoodOrderResponse>>> getByUser(
            @RequestParam int userId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        List<FoodOrderResponse> list = foodOrderService.findByUserId(userId, new PageRequest(page, size))
                .stream()
                .map(FoodOrderMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("User orders fetched", list));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<FoodOrderResponse>>> getByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        List<FoodOrderResponse> list = foodOrderService.findByStatus(status, new PageRequest(page, size))
                .stream()
                .map(FoodOrderMapper::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Orders by status fetched", list));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FoodOrderResponse>> create(@Validated @RequestBody CreateOrderRequest request) {
        logger.info("Creating order for userId={}", request.userId());
        FoodOrder saved = foodOrderService.save(FoodOrderMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created", FoodOrderMapper.toResponse(saved)));
    }

    @PutMapping("/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(@Validated @RequestBody UpdateOrderStatusRequest request) {
        foodOrderService.updateStatus(request.orderId(), OrderStatus.valueOf(request.status().toUpperCase()));
        return ResponseEntity.ok(ApiResponse.success("Order status updated", null));
    }

    @PutMapping("/payment-status")
    public ResponseEntity<ApiResponse<Void>> updatePaymentStatus(@Validated @RequestBody UpdatePaymentStatusRequest request) {
        foodOrderService.updatePaymentStatus(request.orderId(), request.isPaid());
        return ResponseEntity.ok(ApiResponse.success("Payment status updated", null));
    }

    @PutMapping("/payment-mode")
    public ResponseEntity<ApiResponse<Void>> updatePaymentMode(@Validated @RequestBody UpdatePaymentModeRequest request) {
        foodOrderService.updatePaymentMode(request.orderId(), request.paymentMode());
        return ResponseEntity.ok(ApiResponse.success("Payment mode updated", null));
    }
}
